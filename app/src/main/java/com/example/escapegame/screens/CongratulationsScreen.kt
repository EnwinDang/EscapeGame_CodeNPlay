package com.example.escapegame.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.escapegame.R
import com.example.escapegame.viewmodel.Difficulty

@Composable
fun CongratulationsScreen(
    totalTime: Long,
    difficulty: Difficulty?,
    onPlayAgain: () -> Unit
) {
    BackHandler(enabled = true) { /* back disabled during game */ }

    val totalSeconds = totalTime / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    val difficultyLabel = when (difficulty) {
        Difficulty.KIDS -> stringResource(R.string.difficulty_kids)
        Difficulty.TEENS -> stringResource(R.string.difficulty_teens)
        null -> ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.congrats_title),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.congrats_subtitle),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = stringResource(R.string.congrats_time, minutes, seconds),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        if (difficultyLabel.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.congrats_difficulty, difficultyLabel),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onPlayAgain,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_play_again))
        }
    }
}
