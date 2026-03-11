package com.example.escapegame.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.escapegame.logic.JUNIOR_AGENT
import com.example.escapegame.logic.MissionConfig
import com.example.escapegame.logic.SENIOR_AGENT

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

    val missionConfig: MissionConfig
        get() = if (difficulty == Difficulty.KIDS) JUNIOR_AGENT else SENIOR_AGENT

    fun reset() {
        difficulty = null
        startTime = 0L
    }
}
