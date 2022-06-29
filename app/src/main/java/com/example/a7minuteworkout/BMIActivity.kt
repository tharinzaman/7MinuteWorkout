package com.example.a7minuteworkout

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.a7minuteworkout.databinding.ActivityBmiactivityBinding
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import java.text.DecimalFormat

class BMIActivity : YouTubeBaseActivity() {

    val api_key = "AIzaSyCgUBNX1Cy-wJNCAZw2FI05FOp8mzKGr5I"

    private var binding: ActivityBmiactivityBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiactivityBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.ytVideo?.initialize(api_key, object : YouTubePlayer.OnInitializedListener{
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider?,
                player: YouTubePlayer?,
                p2: Boolean
            ) {
                player?.loadVideo("gaaTW0Elxgg")
                player?.play()
            }
            override fun onInitializationFailure(
                p0: YouTubePlayer.Provider?,
                p1: YouTubeInitializationResult?
            ) {
                Toast.makeText(this@BMIActivity, "Failed", Toast.LENGTH_SHORT).show()
            }
        })

        binding?.toolbarBMI?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.rbMetricUnits?.setOnClickListener {
            // Make the metric layout visible and the us layout invisible
            binding?.llMetric?.visibility = View.VISIBLE
            binding?.llUS?.visibility = View.INVISIBLE

            // Clear the US layout edit texts
            binding?.etUSWeight?.text?.clear()
            binding?.etUSFeet?.text?.clear()
            binding?.etUSInches?.text?.clear()

            // Make metric unit checked and us unit unchecked
            binding?.rbMetricUnits?.isChecked = true
            binding?.rbUSUnits?.isChecked = false
        }
        binding?.rbUSUnits?.setOnClickListener {
            // Make the us layout visible and the metric layout invisible
            binding?.llUS?.visibility = View.VISIBLE
            binding?.llMetric?.visibility = View.INVISIBLE

            // Clear the metric layout edit texts
            binding?.etMetricWeight?.text?.clear()
            binding?.etMetricHeight?.text?.clear()

            // Make the us unit checked and the metric unit unchecked
            binding?.rbUSUnits?.isChecked = true
            binding?.rbMetricUnits?.isChecked = false
        }

        binding?.btnCalculate?.setOnClickListener {
            var bmi : Double? = null
            var df = DecimalFormat("0.00")
            // If the metric system is selected:
            if (binding?.rbMetricUnits?.isChecked == true){
                // Check if there are valid details entered and not empty:
                if (checkDetails()) {
                    // Calculate the BMI
                    bmi = calculateBMI()
                    // Set the BMI text:
                    binding?.tvBMI?.text = df.format(bmi)
                    // Set the BMI classification and advice depending on the BMI calculated
                    displayBMIResults(bmi)
                    // Make the BMI results screen visible
                    binding?.llBMI?.visibility = View.VISIBLE
                }
            }
            // If the us system is selected:
            if (binding?.rbUSUnits?.isChecked == true){
                // Check if there are valid details entered and not empty:
                if (checkDetails()) {
                    // Calculate the BMI
                    bmi = calculateBMI()
                    // Set the BMI text:
                    binding?.tvBMI?.text = df.format(bmi)
                    // Set the BMI classification and advice depending on the BMI calculated
                    displayBMIResults(bmi)
                    // Make the BMI results screen visible
                    binding?.llBMI?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun checkDetails() : Boolean{
        var isValid = true
        // If the metric system is selected:
        if (binding?.rbMetricUnits?.isChecked == true){
            if (binding?.etMetricHeight?.text.isNullOrEmpty()){
                isValid = false
                Toast.makeText(this@BMIActivity, "Please enter a valid height", Toast.LENGTH_SHORT).show()
            }
            if (binding?.etMetricWeight?.text.isNullOrEmpty()){
                isValid = false
                Toast.makeText(this@BMIActivity, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
            }
        }
        // If the US system is selected:
        else if (binding?.rbUSUnits?.isChecked == true){
            if (binding?.etUSWeight?.text.isNullOrEmpty()){
                isValid = false
                Toast.makeText(this@BMIActivity, "Please enter a valid weight", Toast.LENGTH_SHORT).show()
            }
            if (binding?.etUSFeet?.text.isNullOrEmpty()){
                isValid = false
                Toast.makeText(this@BMIActivity, "Please enter a valid height for feet", Toast.LENGTH_SHORT).show()
            }
            if (binding?.etUSInches?.text.isNullOrEmpty() || binding?.etUSInches?.text.toString().toInt() < 0
                || binding?.etUSInches?.text.toString().toInt() > 11){
                isValid = false
                Toast.makeText(this@BMIActivity, "Please enter a valid height for inches", Toast.LENGTH_SHORT).show()
            }
        }
        return isValid
    }

    private fun calculateBMI() : Double? {
        var bmi : Double? = null
        var height: Double?
        var weight : Double?

        // If the metric system is selected
        if (binding?.rbMetricUnits?.isChecked == true){
            height = binding?.etMetricHeight?.text.toString().toDouble()
            weight = binding?.etMetricWeight?.text.toString().toDouble()
            bmi = weight / ((height / 100) * (height / 100))
        }
        // If the US system is selected
        else if (binding?.rbUSUnits?.isChecked == true){
            weight = binding?.etUSWeight?.text.toString().toDouble()
            height = ((binding?.etUSFeet?.text.toString().toDouble() * 12) + binding?.etUSInches?.text.toString().toDouble())
            bmi = (weight / (height * height)) * 703
        }
        return bmi
    }

    private fun displayBMIResults(bmi : Double?){
        if (bmi != null) {
            when {
                (bmi > 0 && bmi < 18) -> {
                    binding?.tvClassification?.text = "Underweight"
                    binding?.tvAdvice?.text = getString(R.string.underweight_advice)
                }
                (bmi > 18 && bmi < 25) -> {
                    binding?.tvClassification?.text = "Healthy"
                    binding?.tvAdvice?.text = getString(R.string.healthy_advice)
                }
                (bmi > 25 && bmi < 30) -> {
                    binding?.tvClassification?.text = "Overweight"
                    binding?.tvAdvice?.text = getString(R.string.overweight_advice)
                }
                (bmi > 30) -> {
                    binding?.tvClassification?.text = "Obese"
                    binding?.tvAdvice?.text = getString(R.string.obese_advice)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}