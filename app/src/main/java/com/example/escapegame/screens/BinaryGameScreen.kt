package com.example.escapegame.screens

import android.media.MediaPlayer
import com.example.escapegame.logic.VideoAssetManager
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.escapegame.R
import com.example.escapegame.logic.BinaryPuzzle
import com.example.escapegame.theme.EscapeGameTheme

import com.example.escapegame.theme.MissionControlBackground
import kotlinx.coroutines.delay

@Composable
fun BinaryGameScreen(
    videoAssetManager: VideoAssetManager,
    timerSeconds: Int,
    onSolved: (String) -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) { /* back disabled during game */ }

    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    var isBubblePlaying by remember { mutableStateOf(true) }

    val locale = if (isPreview) "en"
                 else AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
    val audioPath = "audio/binary_$locale.mp3"

    val mediaPlayer: MediaPlayer? = remember {
        if (isPreview) null
        else try {
            MediaPlayer().apply {
                try {
                    val afd = context.assets.openFd(audioPath)
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                } catch (_: Exception) {
                    try {
                        val afd = context.assets.openFd("audio/test.mp3")
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    } catch (_: Exception) { }
                }
                try { prepare(); start() } catch (_: Exception) { }
                setOnCompletionListener { isBubblePlaying = false }
            }
        } catch (_: Exception) { null }
    }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer?.release() }
    }

    val puzzle = remember { BinaryPuzzle().also { it.generatePuzzle() } }
    val sectorId = remember { (100..999).random() }
    var userAnswer by remember { mutableStateOf("") }
    var solved by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    var shakeKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(shakeKey) {
        if (shakeKey == 0) return@LaunchedEffect
        shakeOffset.snapTo(0f)
        shakeOffset.animateTo(
            targetValue = 0f,
            animationSpec = keyframes {
                durationMillis = 400
                20f at 50; (-20f) at 100; 20f at 150; (-20f) at 200
                10f at 250; (-10f) at 300; 0f at 400
            }
        )
    }

    var timeLeft by remember { mutableIntStateOf(timerSeconds) }
    var timerExpired by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000L)
            if (solved) break
            timeLeft--
        }
        if (!solved && timeLeft == 0) timerExpired = true
    }

    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timerText = "%02d:%02d".format(minutes, seconds)

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            when {
                solved -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(40.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.binary_solved_title),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.binary_code_word_label),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = puzzle.currentWord,
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { onSolved(puzzle.currentWord) },
                            modifier = Modifier.fillMaxWidth().height(72.dp)
                        ) {
                            Text(stringResource(R.string.binary_continue_quiz), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }

                timerExpired -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(40.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.binary_times_up),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(32.dp))
                        Button(
                            onClick = { onSolved(puzzle.currentWord) },
                            modifier = Modifier.fillMaxWidth().height(72.dp)
                        ) {
                            Text(stringResource(R.string.binary_continue_anyway), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { translationX = shakeOffset.value }
                    ) {
                        // ── Top header bar ──────────────────────────────────────
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(horizontal = 24.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${stringResource(R.string.binary_sector_label)} $sectorId",
                                style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = timerText,
                                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace),
                                color = if (timeLeft <= 60) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // ── Main content ────────────────────────────────────────
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .padding(24.dp),
                            horizontalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Left sidebar — decode controls
                            Column(
                                modifier = Modifier
                                    .weight(0.38f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "DECODE PARAMETERS",
                                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(R.string.binary_decode_prompt),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.8f)
                                )

                                // Binary sequence display
                                Surface(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = MaterialTheme.shapes.medium,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = puzzle.currentBinary,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 2.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(12.dp).fillMaxWidth()
                                    )
                                }

                                Spacer(Modifier.weight(1f))

                                // Answer input
                                OutlinedTextField(
                                    value = userAnswer,
                                    onValueChange = { userAnswer = it },
                                    label = {
                                        Text(
                                            stringResource(R.string.binary_answer_label),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    textStyle = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                        focusedTextColor = MaterialTheme.colorScheme.primary,
                                        unfocusedTextColor = MaterialTheme.colorScheme.primary
                                    )
                                )

                                // AISpeechBubble audio control
                                AISpeechBubble(
                                    videoAssetManager = videoAssetManager,
                                    isPlaying = isBubblePlaying,
                                    onPlay = {
                                        if (!isBubblePlaying) {
                                            mediaPlayer?.start()
                                            isBubblePlaying = true
                                        } else {
                                            mediaPlayer?.pause()
                                            isBubblePlaying = false
                                        }
                                    },
                                    modifier = Modifier.size(80.dp).align(Alignment.CenterHorizontally)
                                )
                            }

                            // Right — binary grid + action buttons
                            Column(
                                modifier = Modifier
                                    .weight(0.62f)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                BinaryGridCard(
                                    word = puzzle.currentWord,
                                    modifier = Modifier.weight(1f).fillMaxWidth()
                                )

                                // Action buttons
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = { userAnswer = "" },
                                        modifier = Modifier.weight(1f).height(56.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(
                                            stringResource(R.string.binary_reset_grid),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            if (puzzle.checkAnswer(userAnswer)) solved = true
                                            else shakeKey++
                                        },
                                        modifier = Modifier.weight(1f).height(56.dp)
                                    ) {
                                        Icon(Icons.Filled.LockOpen, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            stringResource(R.string.binary_verify_protocol),
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            HomeButton(onHome = onHome, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp))
        }
    }
}

@Composable
private fun BinaryGridCard(word: String, modifier: Modifier = Modifier) {
    val cyan = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f)),
        border = BorderStroke(2.dp, cyan)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            word.forEach { char ->
                val bits = char.code.toString(2).padStart(8, '0')
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    bits.forEach { bit ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .background(
                                    if (bit == '1') cyan.copy(alpha = 0.85f)
                                    else Color.Black.copy(alpha = 0.7f)
                                )
                                .border(
                                    1.dp,
                                    if (bit == '1') cyan else cyan.copy(alpha = 0.2f)
                                )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 900, heightDp = 560)
@Composable
private fun BinaryGameScreenPreview() {
    val context = LocalContext.current
    EscapeGameTheme {
        BinaryGameScreen(
            videoAssetManager = VideoAssetManager(context),
            timerSeconds = 300,
            onSolved = {},
            onHome = {}
        )
    }
}
