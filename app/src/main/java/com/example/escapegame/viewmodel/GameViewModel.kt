package com.example.escapegame.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.escapegame.logic.JUNIOR_AGENT
import com.example.escapegame.logic.MissionConfig
import com.example.escapegame.logic.SENIOR_AGENT
import com.example.escapegame.logic.VideoAssetManager
import com.example.escapegame.logic.VideoUpdateManager
import kotlinx.coroutines.launch

enum class Difficulty { KIDS, TEENS }

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val videoAssetManager = VideoAssetManager(application)

    init {
        // Check for updated videos in the background on every app launch.
        // Runs silently — failures are swallowed and bundled assets serve as fallback.
        viewModelScope.launch {
            VideoUpdateManager(application).checkAndUpdate()
        }
    }

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
