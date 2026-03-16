package com.example.escapegame.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryFull
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.escapegame.R
import com.example.escapegame.theme.MissionControlBackground
import kotlinx.coroutines.delay

private data class SymbolGroup(val normal: ImageVector, val intruder: ImageVector)

private val symbolGroups = listOf(
    SymbolGroup(normal = Icons.Filled.BatteryFull,  intruder = Icons.Filled.BatteryAlert),
    SymbolGroup(normal = Icons.Filled.Shield,        intruder = Icons.Filled.Warning),
    SymbolGroup(normal = Icons.Filled.Wifi,          intruder = Icons.Filled.WifiOff),
    SymbolGroup(normal = Icons.Filled.SmartToy,      intruder = Icons.Filled.BugReport),
    SymbolGroup(normal = Icons.Filled.Lock,          intruder = Icons.Filled.LockOpen),
)

private data class GameRound(val symbols: List<ImageVector>, val intruderIndex: Int)

private fun generateRound(attemptKey: Int): GameRound {
    val group = symbolGroups[attemptKey % symbolGroups.size]
    val list = MutableList(5) { group.normal }.also { it.add(group.intruder) }
    list.shuffle()
    return GameRound(list, list.indexOf(group.intruder))
}

@Composable
fun AiGameScreen(
    onGameCompleted: () -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) {}

    val totalRounds = 5
    var correctCount by remember { mutableIntStateOf(0) }
    var attemptKey by remember { mutableIntStateOf(0) }
    var gameWon by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }
    var shakeKey by remember { mutableIntStateOf(0) }

    val round = remember(attemptKey) { generateRound(attemptKey) }
    var timeProgress by remember(attemptKey) { mutableFloatStateOf(1f) }

    LaunchedEffect(attemptKey) {
        if (gameWon) return@LaunchedEffect
        val durationMs = maxOf(8000L - correctCount * 500L, 3000L)
        val steps = 60
        val stepDelay = durationMs / steps
        for (i in 1..steps) {
            delay(stepDelay)
            if (gameWon) return@LaunchedEffect
            timeProgress = 1f - i.toFloat() / steps
        }
        shakeKey++
        delay(500L)
        attemptKey++
    }

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
                when {
                    gameWon -> {
                        Text(
                            text = stringResource(R.string.ai_game_won_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.ai_game_won_body),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onGameCompleted,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.btn_continue))
                        }
                    }

                    else -> {
                        Text(
                            text = stringResource(R.string.ai_game_step_label),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "$correctCount / $totalRounds",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.ai_game_find_intruder),
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { timeProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp),
                            color = if (timeProgress > 0.4f) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.error,
                        )
                        Spacer(modifier = Modifier.height(32.dp))

                        // 3×2 icon grid
                        round.symbols.chunked(3).forEachIndexed { rowIdx, rowSymbols ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                rowSymbols.forEachIndexed { colIdx, icon ->
                                    val actualIndex = rowIdx * 3 + colIdx
                                    Button(
                                        onClick = {
                                            if (actualIndex == round.intruderIndex) {
                                                correctCount++
                                                if (correctCount >= totalRounds) {
                                                    gameWon = true
                                                } else {
                                                    attemptKey++
                                                }
                                            } else {
                                                shakeKey++
                                            }
                                        },
                                        modifier = Modifier.size(120.dp)
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(64.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            HomeButton(
                onHome = onHome,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
        }
    }
}