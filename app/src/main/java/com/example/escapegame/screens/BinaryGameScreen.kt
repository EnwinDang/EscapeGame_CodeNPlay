package com.example.escapegame.screens

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.logic.BinaryPuzzle
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground
import kotlinx.coroutines.delay

@Composable
fun BinaryGameScreen(
    timerSeconds: Int,
    onSolved: (String) -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) { /* back disabled during game */ }

    val context = LocalContext.current
    var isBubblePlaying by remember { mutableStateOf(true) }
    val mediaPlayer = remember {
        val afd = context.assets.openFd("audio/test.mp3")
        MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
            setOnCompletionListener { isBubblePlaying = false }
        }
    }
    DisposableEffect(Unit) {
        onDispose { mediaPlayer.release() }
    }

    val puzzle = remember { BinaryPuzzle().also { it.generatePuzzle() } }
    var userAnswer by remember { mutableStateOf("") }
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
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.binary_code_word_label),
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = puzzle.currentWord,
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { onSolved(puzzle.currentWord) },
                            modifier = Modifier.fillMaxWidth().height(72.dp)
                        ) {
                            Text(stringResource(R.string.binary_continue_quiz), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    timerExpired -> {
                        Text(
                            text = stringResource(R.string.binary_times_up),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { onSolved(puzzle.currentWord) },
                            modifier = Modifier.fillMaxWidth().height(72.dp)
                        ) {
                            Text(stringResource(R.string.binary_continue_anyway), style = MaterialTheme.typography.titleLarge)
                        }
                    }
                    else -> {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BinaryGridCard(word = puzzle.currentWord, modifier = Modifier.weight(0.45f).fillMaxHeight(0.8f))
                            
                            Column(
                                modifier = Modifier.weight(0.55f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = timerText,
                                    style = MaterialTheme.typography.displayLarge.copy(fontFamily = FontFamily.Monospace),
                                    color = if (timeLeft <= 60) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = stringResource(R.string.binary_decode_prompt),
                                    style = MaterialTheme.typography.headlineLarge,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Grote, duidelijke binaire tekst
                                Surface(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    shape = MaterialTheme.shapes.medium,
                                    border = BorderStroke(1.dp, MatrixGreen.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = puzzle.currentBinary,
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            letterSpacing = 4.sp
                                        ),
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(32.dp))
                                
                                if (locked) {
                                    IconButton(
                                        onClick = { locked = false },
                                        modifier = Modifier.size(120.dp).background(MatrixGreen.copy(alpha = 0.1f), MaterialTheme.shapes.extraLarge)
                                    ) {
                                        Icon(
                                            Icons.Filled.Lock,
                                            contentDescription = null,
                                            modifier = Modifier.size(80.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                } else {
                                    // Invoerveld mooier en groter maken
                                    OutlinedTextField(
                                        value = userAnswer,
                                        onValueChange = { userAnswer = it },
                                        label = { Text(stringResource(R.string.binary_answer_label), style = MaterialTheme.typography.titleMedium) },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true,
                                        textStyle = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            textAlign = TextAlign.Center
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MatrixGreen,
                                            unfocusedBorderColor = MatrixGreen.copy(alpha = 0.5f),
                                            focusedLabelColor = MatrixGreen
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
                                    Button(
                                        onClick = { if (puzzle.checkAnswer(userAnswer)) { solved = true } else { shakeKey++ } },
                                        modifier = Modifier.fillMaxWidth().height(72.dp)
                                    ) {
                                        Icon(Icons.Filled.LockOpen, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(stringResource(R.string.btn_submit), style = MaterialTheme.typography.titleLarge)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            HomeButton(onHome = onHome, modifier = Modifier.align(Alignment.TopEnd).padding(16.dp))
            AISpeechBubble(
                isPlaying = isBubblePlaying,
                onPlay = {
                    mediaPlayer.seekTo(0)
                    mediaPlayer.start()
                    isBubblePlaying = true
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 80.dp, end = 64.dp)
            )
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
            verticalArrangement = Arrangement.Center,
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
                                .background(if (bit == '1') Color.Black else Color.White)
                                .border(1.dp, Color(0xFF444444))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}
