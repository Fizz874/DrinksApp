package com.listofdrinks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    var timeleft by  mutableLongStateOf(60000) // Pozostały czas w milisekundach
    var isRunning by mutableStateOf(false)  // Czy minutnik działa?
    var isFinished by mutableStateOf(false)  // Czy minutnik zakończył odliczanie
    var initialTime by mutableLongStateOf(60000)  // Początkowy czas określony przez użytkownika
    var lastUpdateTime by  mutableLongStateOf(0)  // Początkowy czas określony przez użytkownika




    fun setTimer(inputMinutes:String, inputSeconds:String){
        isRunning = false
        isFinished = false
        val minutes = inputMinutes.toIntOrNull() ?: 0
        val seconds = inputSeconds.toIntOrNull() ?: 0
        initialTime =
            ((minutes * 60 + seconds) * 1000).toLong() // Zapisanie początkowego czasu
        timeleft = initialTime
    }


    fun runTheTimer(){
        isRunning = true
        viewModelScope.launch {
            lastUpdateTime = System.currentTimeMillis()
            while (isRunning && timeleft > 0) {
                val currentTime = System.currentTimeMillis()
                val elapsedTime = currentTime - lastUpdateTime
                timeleft -= elapsedTime

                if (timeleft <= 0) {
                    timeleft = 0
                    isRunning = false
                    isFinished = true
                    break
                }

                lastUpdateTime = currentTime
                delay(100L) // odświeżanie co 100 ms dla płynności
            }

            if (timeleft <= 0) {
                isRunning = false
                isFinished = true
            }
        }
    }


    fun refresh(){
        isRunning = false
        timeleft = initialTime // Resetuj do wprowadzonego czasu
    }

    fun pause(){
        isRunning = false
    }




}