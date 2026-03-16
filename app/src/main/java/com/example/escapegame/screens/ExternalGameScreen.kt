package com.example.escapegame.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.escapegame.R
import com.example.escapegame.theme.MissionControlBackground

@Composable
fun ExternalGameScreen(
    stepNumber: Int,
    title: String,
    instructions: String,
    correctCode: String,
    onCodeCorrect: () -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) { /* back disabled during game */ }

    var enteredCode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var locked by remember { mutableStateOf(true) }
    val shakeOffset = remember { Animatable(0f) }
    var shakeKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(shakeKey) {
        if (shakeKey == 0) return@LaunchedEffect
        shakeOffset.snapTo(0f)
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 400
                20f at 50
                (-20f) at 100
                20f at 150
                (-20f) at 200
                10f at 250
                (-10f) at 300
                0f at 400
            }
        )
    }

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
                    .graphicsLayer { translationX = shakeOffset.value },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.external_step_label, stepNumber, title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = instructions,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (locked) {
                    IconButton(
                        onClick = { locked = false },
                        modifier = Modifier.size(96.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Unlock to enter code",
                            modifier = Modifier.size(72.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = {
                            enteredCode = it
                            showError = false
                        },
                        label = { Text(stringResource(R.string.external_enter_code_label)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (enteredCode.trim().uppercase() == correctCode) {
                                onCodeCorrect()
                            } else {
                                showError = true
                                shakeKey++
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    ) {
                        Text(stringResource(R.string.btn_submit), style = MaterialTheme.typography.titleMedium)
                    }

                    if (showError) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.external_wrong_code),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            HomeButton(
                onHome = onHome,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            )
        }
    }
}
