package com.example.escapegame.screens

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.logic.BinaryPuzzle
import com.example.escapegame.logic.VideoAssetManager
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandYellow
import com.example.escapegame.theme.EscapeGameTheme
import com.example.escapegame.theme.ErrorRed
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground
import kotlinx.coroutines.delay

@Composable
fun BinaryGameScreen(
    videoAssetManager: VideoAssetManager,
    timerSeconds: Int,
    onSolved: (String) -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) { }

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

    DisposableEffect(Unit) { onDispose { mediaPlayer?.release() } }

    val puzzle = remember { BinaryPuzzle().also { it.generatePuzzle() } }
    var userAnswer by remember { mutableStateOf("") }
    var solved by remember { mutableStateOf(false) }
    var feedback by remember { mutableStateOf<Boolean?>(null) }   // true=correct, false=wrong
    val shakeOffset = remember { Animatable(0f) }
    var shakeKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(shakeKey) {
        if (shakeKey == 0) return@LaunchedEffect
        shakeOffset.snapTo(0f)
        shakeOffset.animateTo(0f, keyframes {
            durationMillis = 400
            20f at 50; (-20f) at 100; 20f at 150; (-20f) at 200
            10f at 250; (-10f) at 300; 0f at 400
        })
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

    fun submit() {
        if (puzzle.checkAnswer(userAnswer)) {
            feedback = true
            solved = true
        } else {
            feedback = false
            shakeKey++
        }
    }

    MissionControlBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // ── Top bar ───────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HomeButton(onHome = onHome)

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    MissionProgressBar(activeMission = 0)
                }

                BinaryTimerPanel(timeLeft = timeLeft, totalSeconds = timerSeconds)
            }

            // ── Main area ─────────────────────────────────────────────────────
            when {
                solved -> {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✓", fontSize = 64.sp, color = MatrixGreen)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.binary_solved_title),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MatrixGreen,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${stringResource(R.string.binary_code_word_label)} ${puzzle.currentWord}",
                            fontSize = 20.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))
                        Box(
                            modifier = Modifier
                                .width(280.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(BrandBlue, MatrixGreen)))
                                .clickable { onSolved(puzzle.currentWord) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.binary_continue_quiz),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                timerExpired -> {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.binary_times_up),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ErrorRed,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))
                        Box(
                            modifier = Modifier
                                .width(280.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Brush.horizontalGradient(listOf(BrandBlue, MatrixGreen)))
                                .clickable { onSolved(puzzle.currentWord) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(R.string.binary_continue_anyway),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                else -> {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .graphicsLayer { translationX = shakeOffset.value },
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ── Left panel (2/5) ──────────────────────────────────
                        Column(
                            modifier = Modifier
                                .weight(2f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Speech bubble + hint
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                AISpeechBubble(
                                    videoAssetManager = videoAssetManager,
                                    isPlaying = isBubblePlaying,
                                    onPlay = {
                                        if (!isBubblePlaying) {
                                            mediaPlayer?.start(); isBubblePlaying = true
                                        } else {
                                            mediaPlayer?.pause(); isBubblePlaying = false
                                        }
                                    },
                                    modifier = Modifier.size(110.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White.copy(alpha = 0.06f))
                                        .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.binary_hint_message),
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            // Mission label
                            Text(
                                text = stringResource(R.string.binary_mission_label),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = BrandBlue,
                                letterSpacing = 1.5.sp,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Binary string (text reference)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF060F08))
                                    .border(1.dp, MatrixGreen.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = puzzle.currentBinary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MatrixGreen,
                                    textAlign = TextAlign.Center,
                                    letterSpacing = 1.5.sp
                                )
                            }

                            // Input field
                            OutlinedTextField(
                                value = userAnswer,
                                onValueChange = { userAnswer = it },
                                placeholder = {
                                    Text(
                                        stringResource(R.string.binary_input_placeholder),
                                        color = Color.White.copy(alpha = 0.3f),
                                        fontSize = 15.sp
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = when (feedback) {
                                        true  -> MatrixGreen
                                        false -> ErrorRed
                                        null  -> Color.White
                                    }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = when (feedback) {
                                        true  -> MatrixGreen
                                        false -> ErrorRed
                                        null  -> BrandBlue
                                    },
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                                    focusedContainerColor = Color(0xFF060F08),
                                    unfocusedContainerColor = Color(0xFF060F08),
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )

                            // Feedback
                            when (feedback) {
                                true  -> Text("✓ ${stringResource(R.string.binary_correct_feedback)}",
                                    fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                    color = MatrixGreen, textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth())
                                false -> Text("✗ ${stringResource(R.string.binary_incorrect_feedback)}",
                                    fontSize = 13.sp, fontWeight = FontWeight.Bold,
                                    color = ErrorRed, textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth())
                                null  -> {}
                            }
                        }

                        // ── Right panel (3/5) ─────────────────────────────────
                        Column(
                            modifier = Modifier
                                .weight(3f)
                                .fillMaxHeight(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Visual binary grid
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF0A1A0A).copy(alpha = 0.88f))
                                    .border(1.dp, MatrixGreen.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                BinaryGrid(binaryString = puzzle.currentBinary)
                            }

                            // Reset + Verify buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .border(1.5.dp, MatrixGreen.copy(alpha = 0.5f), RoundedCornerShape(50.dp))
                                        .clickable { userAnswer = ""; feedback = null },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.Refresh, null, Modifier.size(15.dp), tint = MatrixGreen)
                                        Text("RESET", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold,
                                            color = MatrixGreen, letterSpacing = 1.5.sp)
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(MatrixGreen)
                                        .clickable { submit() },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(Icons.Filled.Lock, null, Modifier.size(15.dp), tint = Color.Black)
                                        Text("VERIFY PROTOCOL", fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black, letterSpacing = 1.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Binary reference roster ───────────────────────────────────────────────────

// ── Binary visual grid ────────────────────────────────────────────────────────

@Composable
private fun BinaryGrid(binaryString: String, modifier: Modifier = Modifier) {
    val bytes = remember(binaryString) { binaryString.split(" ") }
    val numRows = bytes.size
    val numCols = 8
    val gapH = 8.dp
    val gapV = 20.dp

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableWidth  = maxWidth
        val availableHeight = maxHeight
        val squareFromWidth  = (availableWidth  - gapH * (numCols - 1)) / numCols
        val squareFromHeight = (availableHeight - gapV * (numRows - 1)) / numRows
        val squareSize = minOf(squareFromWidth, squareFromHeight)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(gapV, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            bytes.forEach { byte ->
                Row(horizontalArrangement = Arrangement.spacedBy(gapH)) {
                    byte.forEach { bit -> BitSquare(filled = bit == '1', size = squareSize) }
                }
            }
        }
    }
}

@Composable
private fun BitSquare(filled: Boolean, size: androidx.compose.ui.unit.Dp) {
    val radius = (size * 0.18f)
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(radius))
            .background(if (filled) MatrixGreen else Color.Transparent)
            .border(
                width = 1.5.dp,
                color = if (filled) MatrixGreen else MatrixGreen.copy(alpha = 0.3f),
                shape = RoundedCornerShape(radius)
            )
    ) {
        if (filled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(listOf(Color.White.copy(alpha = 0.18f), Color.Transparent))
                    )
            )
        }
    }
}

// ── Mission progress bar ──────────────────────────────────────────────────────

@Composable
fun MissionProgressBar(activeMission: Int) {
    val missions = listOf("BINARY", "SCRATCH", "AI", "ROBOT")
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        missions.forEachIndexed { index, name ->
            val isActive = index == activeMission
            val isDone   = index < activeMission

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isActive -> Color.Transparent
                                else     -> Color.White.copy(alpha = 0.08f)
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = when {
                                isActive -> MatrixGreen
                                isDone   -> MatrixGreen.copy(alpha = 0.5f)
                                else     -> Color.White.copy(alpha = 0.2f)
                            },
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isActive) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MatrixGreen
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = name,
                    fontSize = 10.sp,
                    fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Normal,
                    color = if (isActive) MatrixGreen else Color.White.copy(alpha = 0.35f),
                    letterSpacing = 0.5.sp
                )
            }

            if (index < missions.lastIndex) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(1.dp)
                        .padding(bottom = 16.dp)
                        .background(Color.White.copy(alpha = 0.15f))
                )
            }
        }
    }
}

// ── Timer panel ───────────────────────────────────────────────────────────────

@Composable
private fun BinaryTimerPanel(timeLeft: Int, totalSeconds: Int) {
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val fraction = timeLeft.toFloat() / totalSeconds.coerceAtLeast(1)

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.06f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Timer,
                contentDescription = null,
                tint = BrandYellow,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = "%02d:%02d".format(minutes, seconds),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = if (timeLeft <= 60) ErrorRed else Color.White
            )
        }
        Spacer(Modifier.height(6.dp))
        // Progress bar
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.1f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(listOf(BrandBlue, MatrixGreen, BrandYellow))
                    )
            )
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