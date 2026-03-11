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
import com.example.escapegame.theme.MissionControlBackground
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
        Difficulty.KIDS  -> stringResource(R.string.difficulty_kids)
        Difficulty.TEENS -> stringResource(R.string.difficulty_teens)
        null             -> ""
    }

    MissionControlBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.congrats_title),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.congrats_subtitle),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = stringResource(R.string.congrats_time, minutes, seconds),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            if (difficultyLabel.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.congrats_difficulty, difficultyLabel),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(56.dp))

            Button(
                onClick = onPlayAgain,
                modifier = Modifier.fillMaxWidth().height(64.dp)
            ) {
                Text(stringResource(R.string.btn_play_again), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
