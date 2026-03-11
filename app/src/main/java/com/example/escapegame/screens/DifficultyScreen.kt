package com.example.escapegame.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
fun DifficultyScreen(onDifficultySelected: (Difficulty) -> Unit) {
    MissionControlBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.difficulty_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.difficulty_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(56.dp))

            Button(
                onClick = { onDifficultySelected(Difficulty.KIDS) },
                modifier = Modifier.fillMaxWidth().height(72.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_kids),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = { onDifficultySelected(Difficulty.TEENS) },
                modifier = Modifier.fillMaxWidth().height(72.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_teens),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
