package com.example.pomodorotimer

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

enum class Session {
    WORK, SHORT_BREAK, LONG_BREAK
}

class TimerViewModel : ViewModel() {

    // Durations
    private val _workDuration = MutableStateFlow(25 * 60 * 1000L)
    val workDuration: StateFlow<Long> = _workDuration

    private val _shortBreakDuration = MutableStateFlow(5 * 60 * 1000L)
    val shortBreakDuration: StateFlow<Long> = _shortBreakDuration

    private val _longBreakDuration = MutableStateFlow(15 * 60 * 1000L)
    val longBreakDuration: StateFlow<Long> = _longBreakDuration

    // Timer state
    private val _timerValue = MutableStateFlow(_workDuration.value)
    val timerValue: StateFlow<Long> = _timerValue

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _currentSession = MutableStateFlow(Session.WORK)
    val currentSession: StateFlow<Session> = _currentSession

    private val _pomodoroCount = MutableStateFlow(0)
    val pomodoroCount: StateFlow<Int> = _pomodoroCount

    private var countDownTimer: CountDownTimer? = null

    fun setWorkDuration(minutes: Long) {
        _workDuration.value = minutes * 60 * 1000L
        if (_currentSession.value == Session.WORK) {
            resetTimer()
        }
    }

    fun setShortBreakDuration(minutes: Long) {
        _shortBreakDuration.value = minutes * 60 * 1000L
    }

    fun setLongBreakDuration(minutes: Long) {
        _longBreakDuration.value = minutes * 60 * 1000L
    }

    fun startTimer() {
        if (_isRunning.value) return

        _isRunning.value = true
        countDownTimer = object : CountDownTimer(_timerValue.value, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _timerValue.value = millisUntilFinished
            }

            override fun onFinish() {
                _isRunning.value = false
                handleNextSession()
            }
        }.start()
    }

    private fun handleNextSession() {
        if (_currentSession.value == Session.WORK) {
            _pomodoroCount.value++
            if (_pomodoroCount.value % 4 == 0) {
                _currentSession.value = Session.LONG_BREAK
                _timerValue.value = _longBreakDuration.value
            } else {
                _currentSession.value = Session.SHORT_BREAK
                _timerValue.value = _shortBreakDuration.value
            }
        } else {
            _currentSession.value = Session.WORK
            _timerValue.value = _workDuration.value
        }
    }

    fun pauseTimer() {
        if (!_isRunning.value) return

        _isRunning.value = false
        countDownTimer?.cancel()
    }

    fun resetTimer() {
        _isRunning.value = false
        countDownTimer?.cancel()
        _currentSession.value = Session.WORK
        _timerValue.value = _workDuration.value
        _pomodoroCount.value = 0
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
