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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.logic.BinaryPuzzle
import com.example.escapegame.theme.MissionControlBackground
import kotlinx.coroutines.delay

@Composable
fun BinaryGameScreen(
    timerSeconds: Int,
    onSolved: (String) -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) { /* back disabled during game */ }

    val puzzle = remember { BinaryPuzzle().also { it.generatePuzzle() } }
    var binaryText by remember { mutableStateOf(puzzle.currentBinary) }
    var userAnswer by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var solved by remember { mutableStateOf(false) }
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

    var timeLeft by remember { mutableIntStateOf(timerSeconds) }
    var timerExpired by remember { mutableStateOf(false) }

    // Countdown timer — stops when puzzle is solved
    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            if (solved) break
            timeLeft--
        }
        if (!solved && timeLeft == 0) {
            timerExpired = true
        }
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timerText = "%02d:%02d".format(minutes, seconds)

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

        when {
            solved -> {
                Text(
                    text = stringResource(R.string.binary_solved_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.binary_code_word_label),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = puzzle.currentWord,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onSolved(puzzle.currentWord) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.binary_continue_quiz))
                }
            }

            timerExpired -> {
                Text(
                    text = stringResource(R.string.binary_times_up),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.binary_ask_master),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onSolved(puzzle.currentWord) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.binary_continue_anyway))
                }
            }

            else -> {
        // Game header
        Text(
            text = stringResource(R.string.binary_step_label),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = timerText,
            style = MaterialTheme.typography.headlineLarge.copy(fontFamily = FontFamily.Monospace),
            color = if (timeLeft <= 60) MaterialTheme.colorScheme.error
                    else MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.binary_decode_prompt),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = binaryText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 20.sp,
                letterSpacing = 3.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (locked) {
            IconButton(
                onClick = { locked = false },
                modifier = Modifier.size(96.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Unlock to answer",
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            OutlinedTextField(
                value = userAnswer,
                onValueChange = { userAnswer = it },
                label = { Text(stringResource(R.string.binary_answer_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (puzzle.checkAnswer(userAnswer)) {
                        solved = true
                        showError = false
                    } else {
                        showError = true
                        shakeKey++
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.btn_submit))
            }

            if (showError) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.binary_wrong),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                puzzle.generatePuzzle()
                binaryText = puzzle.currentBinary
                userAnswer = ""
                showError = false
                locked = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.btn_new_puzzle))
        }
            } // else
        } // when
    }
    HomeButton(
        onHome = onHome,
        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
    )
    } // Box
    }
}
