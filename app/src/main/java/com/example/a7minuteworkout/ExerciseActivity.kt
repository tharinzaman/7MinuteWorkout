package com.example.a7minuteworkout

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7minuteworkout.databinding.ActivityExerciseBinding
import com.example.a7minuteworkout.databinding.DialogCustomBackConfirmationBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var binding : ActivityExerciseBinding? = null

    private var restTimer : CountDownTimer? = null
    private var restProgress = 0
    private var restTimerDuration : Long = 10
    private var exerciseTimer : CountDownTimer? = null
    private var exerciseTimeLeft: Long = 30
    private var exerciseProgress = 0
    private var exerciseTimerDuration : Long = 30

    private var exerciseList : ArrayList<ExerciseModel>? = null
    private var currentExercisePosition : Int = -1 // Set to -1 as 0 is the first position of arrayList

    private var tts : TextToSpeech? = null
    private var player : MediaPlayer? = null

    private var exerciseAdapter : ExerciseStatusAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Set up the action bar to allow you to go home
        setSupportActionBar(binding?.toolbarExercise)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarExercise?.setNavigationOnClickListener {
            customDialogForBackButton()
        }

        exerciseList = Constants.defaultExerciseList() // Get the exercise list

        tts = TextToSpeech(this, this)


        setupRestView() // Call the method to make the progress bar and text/timer go down
        setUpExerciseStatusRecyclerView()

        binding?.btnPause?.setOnClickListener {
            pauseExercise()
        }
        binding?.btnResume?.setOnClickListener {
            resumeExercise()
        }
    }

    // Make the custom dialog also appear when the bottom back button is pressed
    override fun onBackPressed() {
        customDialogForBackButton()
    }

    private fun customDialogForBackButton(){
        // Create a dialog
        val customDialog = Dialog(this)
        // Inflate the custom back dialog xml file to access its contents via binding:
        val dialogBinding = DialogCustomBackConfirmationBinding.inflate(layoutInflater)
        // Make the custom dialog variable look like the custom back dialog xml
        customDialog.setContentView(dialogBinding.root)
        // Make sure user can't cancel the dialog by pressing outside
        customDialog.setCanceledOnTouchOutside(false)
        dialogBinding.btnYes.setOnClickListener {
            this@ExerciseActivity.finish()
            customDialog.dismiss()
        }
        dialogBinding.btnNo.setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun setUpExerciseStatusRecyclerView(){
        binding?.rvExerciseStatus?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseAdapter = ExerciseStatusAdapter(exerciseList!!)
        binding?.rvExerciseStatus?.adapter = exerciseAdapter
    }

    /* This method is used for resetting the rest timer
    It works by checking if the rest timer exists, if it does then cancelling it, setting the restProgress to 0
    and then calling the setProgressBar method which decrements it
     */
    private fun setupRestView(){

        // Try to play the sound
        try {
            val soundURI = Uri.parse("android.resource://com.example.a7minuteworkout/" +
            R.raw.press_start) // Create the sound variable by finding the sound you want to play
            player = MediaPlayer.create(applicationContext, soundURI) // Create the player variable to actually be able to play the sound
            player?.isLooping = false // So that the sound doesn;t repeat
            player?.start() // Play the sound
        } catch (e : Exception){
            e.printStackTrace()
        }

        // Set exercise views to invisible
        binding?.flExerciseView?.visibility = View.INVISIBLE
        binding?.tvExerciseName?.visibility = View.INVISIBLE
        binding?.ivImage?.visibility = View.INVISIBLE
        binding?.flBtnPause?.visibility = View.INVISIBLE
        // Set exercise views as visible
        binding?.tvTitle?.visibility = View.VISIBLE
        binding?.flRestView?.visibility = View.VISIBLE
        binding?.llUpcomingExercise?.visibility = View.VISIBLE
        binding?.tvUpcomingExercise?.text = exerciseList!![currentExercisePosition + 1].getName()

        if(restTimer != null){ // Check that the timer exists
            restTimer?.cancel() // Cancel the timer if it does exist
            restProgress = 0 // Set the rest progress to 0
        }
        setRestProgressBar() // Call the method to countdown the timer
    }

    // This method will make the progress bar/circle and the progress text view decrease from 10 to 0 on each tick of the timer
    private fun setRestProgressBar(){
        binding?.progressBar?.progress = restProgress

        restTimer = object : CountDownTimer(restTimerDuration*1000, 1000){
            // What happens when the timer ticks:
            override fun onTick(p0: Long) {
                restProgress++ // Increase rest by 1 value
                binding?.progressBar?.progress = 10 - restProgress // Progress bar decreases by 1 each second
                binding?.tvTimer?.text = (10 - restProgress).toString() // Make the progress bar decrementing visible in the textview
            }
            // What happens when the timer finishes:
            override fun onFinish() {
                currentExercisePosition++ // Increment position so we can start the first exercise
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseAdapter!!.notifyDataSetChanged()
                setupExerciseView()
            }
        }.start()
    }

    private fun setupExerciseView(){
        // Set rest views to invisible
        binding?.flRestView?.visibility = View.INVISIBLE
        binding?.tvTitle?.visibility = View.INVISIBLE
        binding?.llUpcomingExercise?.visibility = View.INVISIBLE
        // Set exercise views as visible
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.flExerciseView?.visibility = View.VISIBLE
        binding?.ivImage?.visibility = View.VISIBLE
        binding?.flBtnPause?.visibility = View.VISIBLE

        if(exerciseTimer != null){ // Check that the timer exists
            exerciseTimer?.cancel() // Cancel the timer if it does exist
            exerciseProgress = 0
            exerciseTimeLeft = 30// Set the rest progress to 0
        }

        speakOut(exerciseList!![currentExercisePosition].getName())

        binding?.ivImage?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()

        setExerciseProgressBar() // Call the method to countdown the timer
    }

    private fun setExerciseProgressBar(){
        binding?.progressBarExercise?.progress = exerciseProgress

        exerciseTimer = object : CountDownTimer(exerciseTimerDuration*1000, 1000){
            // What happens when the timer ticks:
            override fun onTick(p0: Long) {
                exerciseProgress++ // Increase rest by 1 value
                exerciseTimeLeft--
                binding?.progressBarExercise?.progress = 30 - exerciseProgress // Progress bar decreases by 1 each second
                binding?.tvTimerExercise?.text = (30 - exerciseProgress).toString() // Make the progress bar decrementing visible in the textview
            }
            // What happens when the timer finishes:
            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                } else {
                    startActivity(Intent(this@ExerciseActivity, FinishActivity::class.java))
                    finish()
                }
            }
        }.start()
    }

    private fun pauseExercise (){
        if (exerciseTimer!= null){
            exerciseTimer?.cancel()
        }
        binding?.btnPause?.visibility = View.GONE
        binding?.btnResume?.visibility = View.VISIBLE
    }

    private fun resumeExercise() {

        binding?.progressBarExercise?.progress = exerciseTimeLeft.toInt()

        binding?.btnPause?.visibility = View.VISIBLE
        binding?.btnResume?.visibility = View.GONE

        exerciseTimer = object : CountDownTimer(exerciseTimeLeft*1000, 1000){

            override fun onTick(p0: Long) {
                exerciseTimeLeft--
                binding?.progressBarExercise?.progress = (exerciseTimeLeft).toInt()
                binding?.tvTimerExercise?.text = (exerciseTimeLeft).toString()
            }

            override fun onFinish() {
                if (currentExercisePosition < exerciseList?.size!! - 1){
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseAdapter!!.notifyDataSetChanged()
                    setupRestView()
                } else {
                    startActivity(Intent(this@ExerciseActivity, FinishActivity::class.java))
                    finish()
                }
            }
        }.start()
    }

    // Method to speak out the text entered
    private fun speakOut(text : String){
        if (tts != null){
            tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS){
            val result = tts?.setLanguage(Locale.ENGLISH)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "Language specified is not supported")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (restTimer != null){
            restTimer?.cancel()
            restProgress = 0
        }

        if (exerciseTimer != null){
            exerciseTimer?.cancel()
            exerciseProgress = 0
        }

        if (tts != null){
            tts!!.stop()
            tts!!.shutdown()
        }

        if (player != null){
            player!!.stop()
        }

        binding = null
    }

}