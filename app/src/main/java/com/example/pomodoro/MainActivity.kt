package com.example.pomodoro

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var resetButton: Button

    private var countDownTimer: CountDownTimer? = null
    private var timeLeftInMillis: Long = 1500000 // 25 minutes
    private var timerRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            timeLeftInMillis = savedInstanceState.getLong("timeLeftInMillis")
            timerRunning = savedInstanceState.getBoolean("timerRunning")
        }

        timerTextView = findViewById(R.id.timer_textview)
        startButton = findViewById(R.id.start_button)
        stopButton = findViewById(R.id.stop_button)
        resetButton = findViewById(R.id.reset_button)

        updateTimerText()

        if (timerRunning) {
            startTimer()
        }

        startButton.setOnClickListener {
            if (!timerRunning) {
                startTimer()
            }
        }

        stopButton.setOnClickListener {
            if (timerRunning) {
                stopTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
            }

            override fun onFinish() {
                timerRunning = false
                // Optionally, add a notification or sound here
            }
        }.start()

        timerRunning = true
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        timerRunning = false
    }

    private fun resetTimer() {
        stopTimer()
        timeLeftInMillis = 1500000
        updateTimerText()
    }

    private fun updateTimerText() {
        val minutes = (timeLeftInMillis / 1000) / 60
        val seconds = (timeLeftInMillis / 1000) % 60
        val timeFormatted = String.format("%02d:%02d", minutes, seconds)
        timerTextView.text = timeFormatted
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("timeLeftInMillis", timeLeftInMillis)
        outState.putBoolean("timerRunning", timerRunning)
    }
}
