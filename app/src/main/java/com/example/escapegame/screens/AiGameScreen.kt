package com.example.escapegame.screens

import android.media.MediaPlayer
import com.example.escapegame.logic.VideoAssetManager
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandGreen
import com.example.escapegame.theme.ErrorRed
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground
import com.example.escapegame.theme.YellowDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Game phases ───────────────────────────────────────────────────────────────

private enum class GamePhase { INTRO, COUNTDOWN, GAME }

// ── Icon-spotting game logic ──────────────────────────────────────────────────

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
    val list  = MutableList(5) { group.normal }.also { it.add(group.intruder) }
    list.shuffle()
    return GameRound(list, list.indexOf(group.intruder))
}

private data class StoryElement(
    val icon: ImageVector,
    val iconColor: Color,
    val titleRes: Int,
    val bodyRes: Int,
    val startTimeMs: Int,
)

private val storyElements = listOf(
    StoryElement(
        icon       = Icons.Filled.Warning,
        iconColor  = ErrorRed,
        titleRes   = R.string.ai_intro_alarm_title,
        bodyRes    = R.string.ai_intro_alarm_body,
        startTimeMs = 0
    ),
    StoryElement(
        icon       = Icons.Filled.BugReport,
        iconColor  = YellowDark,
        titleRes   = R.string.ai_intro_threat_title,
        bodyRes    = R.string.ai_intro_threat_body,
        startTimeMs = 8000
    ),
    StoryElement(
        icon       = Icons.Filled.SmartToy,
        iconColor  = BrandBlue,
        titleRes   = R.string.ai_intro_mission_title,
        bodyRes    = R.string.ai_intro_mission_body,
        startTimeMs = 20000
    ),
)

// ── Main composable ───────────────────────────────────────────────────────────

@Composable
fun AiGameScreen(
    videoAssetManager: VideoAssetManager,
    onGameCompleted: () -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) {}

    val isPreview = LocalInspectionMode.current
    val context   = LocalContext.current
    val scope     = rememberCoroutineScope()

    var gamePhase        by remember { mutableStateOf(GamePhase.INTRO) }
    var countdownLabel   by remember { mutableStateOf("") }
    val goLabel = stringResource(R.string.ai_game_intro_go)

    // Audio
    var isPlayingAudio   by remember { mutableStateOf(true) }
    var hasFinishedAudio by remember { mutableStateOf(false) }
    var currentProgressMs by remember { mutableIntStateOf(0) }

    val locale    = if (isPreview) "en"
                   else AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
    val audioPath = "audio/ai_junior_$locale.mp3"

    val mediaPlayer: MediaPlayer? = remember {
        if (isPreview) null
        else try {
            MediaPlayer().apply {
                try {
                    val afd = context.assets.openFd(audioPath)
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                } catch (_: Exception) {
                    try {
                        val afd = context.assets.openFd("audio/ai_$locale.mp3")
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    } catch (_: Exception) {
                        try {
                            val afd = context.assets.openFd("audio/test.mp3")
                            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                        } catch (_: Exception) { }
                    }
                }
                try { 
                    prepare()
                    start() 
                } catch (_: Exception) { 
                    hasFinishedAudio = true 
                }
                setOnCompletionListener {
                    isPlayingAudio   = false
                    hasFinishedAudio = true
                }
            }
        } catch (_: Exception) { 
            hasFinishedAudio = true
            null 
        }
    }

    DisposableEffect(Unit) { onDispose { mediaPlayer?.release() } }

    LaunchedEffect(isPlayingAudio) {
        if (isPlayingAudio) {
            while (isPlayingAudio) {
                try {
                    currentProgressMs = mediaPlayer?.currentPosition ?: 0
                } catch (_: Exception) { isPlayingAudio = false }
                delay(100L)
            }
        }
    }

    // Game state
    val totalRounds = 5
    var correctCount by remember { mutableIntStateOf(0) }
    var attemptKey   by remember { mutableIntStateOf(0) }
    var gameWon      by remember { mutableStateOf(false) }
    val shakeOffset  = remember { Animatable(0f) }
    var shakeKey     by remember { mutableIntStateOf(0) }
    val round        = remember(attemptKey) { generateRound(attemptKey) }
    var timeProgress by remember(attemptKey) { mutableFloatStateOf(1f) }

    LaunchedEffect(attemptKey, gamePhase) {
        if (gamePhase != GamePhase.GAME || gameWon) return@LaunchedEffect
        val durationMs = maxOf(8000L - correctCount * 500L, 3000L)
        val steps = 60; val stepDelay = durationMs / steps
        for (i in 1..steps) {
            delay(stepDelay)
            if (gamePhase != GamePhase.GAME || gameWon) return@LaunchedEffect
            timeProgress = 1f - i.toFloat() / steps
        }
        shakeKey++; delay(500L); attemptKey++
    }

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

    val currentStoryElement = storyElements.lastOrNull { currentProgressMs >= it.startTimeMs } ?: storyElements[0]

    MissionControlBackground {
        when (gamePhase) {

            // ── INTRO ─────────────────────────────────────────────────────────
            GamePhase.INTRO -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(40.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.ai_game_step_label),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.title_ai_game),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(24.dp))

                        AISpeechBubble(
                            videoAssetManager = videoAssetManager,
                            isPlaying = isPlayingAudio,
                            onPlay = {
                                if (!isPlayingAudio) {
                                    try { mediaPlayer?.start(); isPlayingAudio = true } catch (_: Exception) {}
                                } else {
                                    try { mediaPlayer?.pause(); isPlayingAudio = false } catch (_: Exception) {}
                                }
                            },
                            modifier = Modifier.size(160.dp)
                        )

                        Spacer(Modifier.height(24.dp))

                        AnimatedContent(
                            targetState = currentStoryElement,
                            transitionSpec = { (fadeIn() + scaleIn()) togetherWith (fadeOut() + scaleOut()) },
                            label = "story_anim"
                        ) { element ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(element.titleRes),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = element.iconColor,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = stringResource(element.bodyRes),
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Spacer(Modifier.height(48.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    try { mediaPlayer?.stop() } catch (_: Exception) {}
                                    isPlayingAudio = false
                                    for (step in listOf("3", "2", "1", goLabel)) {
                                        countdownLabel = step
                                        gamePhase = GamePhase.COUNTDOWN
                                        delay(1000L)
                                    }
                                    gamePhase = GamePhase.GAME
                                }
                            },
                            enabled = hasFinishedAudio || isPreview,
                            modifier = Modifier.fillMaxWidth().height(72.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                contentColor = Color.Black,
                                disabledContainerColor = BrandGreen.copy(alpha = 0.3f),
                                disabledContentColor = Color.White.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.ai_intro_btn_start),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    HomeButton(
                        onHome = onHome,
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    )
                }
            }

            // ── COUNTDOWN ─────────────────────────────────────────────────────
            GamePhase.COUNTDOWN -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = countdownLabel,
                        fontSize = 180.sp,
                        fontWeight = FontWeight.Black,
                        color = if (countdownLabel == goLabel) BrandGreen
                                else MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── GAME ──────────────────────────────────────────────────────────
            GamePhase.GAME -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    when {
                        gameWon -> {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(40.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(R.string.ai_game_won_title),
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    text = stringResource(R.string.ai_game_won_body),
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(32.dp))
                                Button(
                                    onClick = onGameCompleted,
                                    modifier = Modifier.fillMaxWidth().height(72.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandGreen,
                                        contentColor = Color.Black
                                    )
                                ) {
                                    Text(
                                        stringResource(R.string.btn_continue),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        else -> {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(40.dp)
                                    .graphicsLayer { translationX = shakeOffset.value },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "$correctCount / $totalRounds",
                                    style = MaterialTheme.typography.displaySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = { timeProgress },
                                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)),
                                    color = if (timeProgress > 0.4f) MatrixGreen else ErrorRed
                                )
                                Spacer(Modifier.height(32.dp))

                                round.symbols.chunked(3).forEach { rowSymbols ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        rowSymbols.forEach { icon ->
                                            val isIntruder = icon == round.symbols[round.intruderIndex]
                                            Button(
                                                onClick = {
                                                    if (isIntruder) {
                                                        correctCount++
                                                        if (correctCount >= totalRounds) gameWon = true
                                                        else attemptKey++
                                                    } else {
                                                        shakeKey++
                                                    }
                                                },
                                                modifier = Modifier.size(120.dp),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                                                border = BorderStroke(1.dp, MatrixGreen.copy(alpha = 0.4f))
                                            ) {
                                                Icon(icon, null, Modifier.size(64.dp), tint = MatrixGreen)
                                            }
                                        }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    HomeButton(
                        onHome = onHome,
                        modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                    )
                }
            }
        }
    }
}
