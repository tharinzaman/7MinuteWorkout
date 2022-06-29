package com.example.a7minuteworkout

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.a7minuteworkout.databinding.ActivityFinishBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class FinishActivity : AppCompatActivity() {

    private var binding : ActivityFinishBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinishBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarFinishActivity)
        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        binding?.toolbarFinishActivity?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnFinish?.setOnClickListener {
            finish()
        }

        val historyDao = (application as WorkoutApp).db.historyDao()
        addDateToDatabase(historyDao)
    }

    private fun addDateToDatabase(historyDao: HistoryDao){

        // Make an instance of calendar
        val c = Calendar.getInstance()
        // Make a dateTime variable from the calendar you created
        val dateTime = c.time

        // Create a simple date format instance
        val sdf  = SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault())
        // Create the date by formatting the dateTime variable
        val date = sdf.format(dateTime)

        lifecycleScope.launch { historyDao.insert(HistoryEntity(date)) }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}