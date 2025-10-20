package com.example.pomodorotimer

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun TimerScreen(timerViewModel: TimerViewModel = viewModel()) {
    val timerValue = timerViewModel.timerValue.collectAsState()
    val isRunning = timerViewModel.isRunning.collectAsState()
    val workDuration = timerViewModel.workDuration.collectAsState()
    val shortBreakDuration = timerViewModel.shortBreakDuration.collectAsState()
    val longBreakDuration = timerViewModel.longBreakDuration.collectAsState()
    val currentSession = timerViewModel.currentSession.collectAsState()
    val pomodoroCount = timerViewModel.pomodoroCount.collectAsState()


    var workDurationInput by remember { mutableStateOf((workDuration.value / 60000).toString()) }
    var shortBreakDurationInput by remember { mutableStateOf((shortBreakDuration.value / 60000).toString()) }
    var longBreakDurationInput by remember { mutableStateOf((longBreakDuration.value / 60000).toString()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Session: ${currentSession.value}")
        Text(text = "Pomodoros: ${pomodoroCount.value}")
        Spacer(modifier = Modifier.height(16.dp))
        Box(contentAlignment = Alignment.Center) {
            val totalDuration = when (currentSession.value) {
                Session.WORK -> workDuration.value.toFloat()
                Session.SHORT_BREAK -> shortBreakDuration.value.toFloat()
                Session.LONG_BREAK -> longBreakDuration.value.toFloat()
            }
            val progress = if (totalDuration > 0) 1f - (timerValue.value / totalDuration) else 1f
            CircularProgressIndicator(
                progress = progress,
                modifier = Modifier.size(200.dp)
            )
            Text(
                text = formatTime(timerValue.value),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { timerViewModel.startTimer() }, enabled = !isRunning.value) {
                Text(text = "Start")
            }
            Button(onClick = { timerViewModel.pauseTimer() }, enabled = isRunning.value) {
                Text(text = "Pause")
            }
            Button(onClick = { timerViewModel.resetTimer() }) {
                Text(text = "Reset")
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = workDurationInput,
                onValueChange = { workDurationInput = it },
                label = { Text("Work Duration (minutes)") }
            )
            TextField(
                value = shortBreakDurationInput,
                onValueChange = { shortBreakDurationInput = it },
                label = { Text("Short Break (minutes)") }
            )
            TextField(
                value = longBreakDurationInput,
                onValueChange = { longBreakDurationInput = it },
                label = { Text("Long Break (minutes)") }
            )
            Button(onClick = {
                val workValue = workDurationInput.toLongOrNull()
                if (workValue != null) {
                    timerViewModel.setWorkDuration(workValue)
                } else {
                    workDurationInput = ""
                }

                val shortBreakValue = shortBreakDurationInput.toLongOrNull()
                if (shortBreakValue != null) {
                    timerViewModel.setShortBreakDuration(shortBreakValue)
                } else {
                    shortBreakDurationInput = ""
                }

                val longBreakValue = longBreakDurationInput.toLongOrNull()
                if (longBreakValue != null) {
                    timerViewModel.setLongBreakDuration(longBreakValue)
                } else {
                    longBreakDurationInput = ""
                }
            }) {
                Text(text = "Set Durations")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TimerScreenPreview() {
    TimerScreen()
}
