package com.example.escapegame.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Difficulty { KIDS, TEENS }

class GameViewModel : ViewModel() {

    var difficulty by mutableStateOf<Difficulty?>(null)
        private set

    var startTime by mutableStateOf(0L)
        private set

    fun startGame(diff: Difficulty) {
        difficulty = diff
        startTime = System.currentTimeMillis()
    }

    fun getTotalTime(): Long {
        return System.currentTimeMillis() - startTime
    }

    fun reset() {
        difficulty = null
        startTime = 0L
    }
}
