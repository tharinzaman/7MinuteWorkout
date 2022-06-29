package com.example.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import com.example.a7minuteworkout.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Create a variable of type binding and set it to null (ActivityMain because that's tis activity)
    private var binding : ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Instantiate the xml file into corresponding View objects
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root) // Use binding.root instead of R.activity.main

        /* It is of type frame layout and not a button because a frame layout was used
        make this 'button', as seen in the XML file.
         */
        binding?.flStart?.setOnClickListener {
            startActivity(Intent(this, ExerciseActivity::class.java))
        }
        binding?.flBMI?.setOnClickListener {
            startActivity(Intent(this, BMIActivity::class.java))
        }
        binding?.flHistory?.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    // Must always override the onDestroy() methods to avoid memory leaks and other problems
    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}