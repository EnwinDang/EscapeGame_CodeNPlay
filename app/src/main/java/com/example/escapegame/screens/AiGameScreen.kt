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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.logic.playSuccessSfx
import com.example.escapegame.logic.playFailSfx
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandGreen
import com.example.escapegame.theme.ErrorRed
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── Game phases ───────────────────────────────────────────────────────────────

private enum class GamePhase { INTRO, COUNTDOWN, GAME }

// ── Emoji-spotting game logic ─────────────────────────────────────────────────

private data class EmojiGroup(val normal: String, val intruder: String)

private data class GameRound(val items: List<String>, val intruderIndex: Int)

private val emojiGroups = listOf(
    EmojiGroup("🤖", "🐛"),
    EmojiGroup("🛡️", "⚠️"),
    EmojiGroup("🔒", "🔓"),
    EmojiGroup("☁️", "⛈️"),
    EmojiGroup("💾", "🗑️"),
)

private fun generateRound(attemptKey: Int): GameRound {
    val group = emojiGroups[attemptKey % emojiGroups.size]
    val intruderIndex = (0..5).random()
    val items = MutableList(6) { group.normal }
    items[intruderIndex] = group.intruder
    return GameRound(items, intruderIndex)
}

// ── UI Components ─────────────────────────────────────────────────────────────

@Composable
private fun EmojiCard(emoji: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.size(120.dp).padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.2f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = emoji, fontSize = 52.sp, textAlign = TextAlign.Center)
        }
    }
}

// ── Main Game Screen ─────────────────────────────────────────────────────────

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

    // Audio Logic
    var isPlayingAudio   by remember { mutableStateOf(true) }
    var hasFinishedAudio by remember { mutableStateOf(false) }
    var currentProgressMs by remember { mutableIntStateOf(0) }

    val locale    = if (isPreview) "en" else AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
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
                        val afd = context.assets.openFd("audio/test.mp3")
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    } catch (_: Exception) { }
                }
                try { 
                    prepare()
                    start() 
                } catch (_: Exception) { hasFinishedAudio = true }
                setOnCompletionListener {
                    isPlayingAudio = false
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
                try { currentProgressMs = mediaPlayer?.currentPosition ?: 0 } catch (_: Exception) { isPlayingAudio = false }
                delay(100L)
            }
        }
    }

    // Game Logic
    val totalRounds = 5
    var correctCount by remember { mutableIntStateOf(0) }
    var attemptKey   by remember { mutableIntStateOf(0) }
    var gameWon      by remember { mutableStateOf(false) }
    val shakeOffset  = remember { Animatable(0f) }
    var shakeKey     by remember { mutableIntStateOf(0) }
    val round        = remember(attemptKey) { generateRound(attemptKey) }
    var timeProgress by remember(attemptKey) { mutableFloatStateOf(1f) }
    var roundTimedOut by remember { mutableStateOf(false) }

    LaunchedEffect(attemptKey, gamePhase) {
        if (gamePhase != GamePhase.GAME || gameWon) return@LaunchedEffect
        val durationMs = maxOf(9000L - correctCount * 800L, 4000L)
        val steps = 100
        for (i in 1..steps) {
            delay(durationMs / steps)
            if (gamePhase != GamePhase.GAME || gameWon) return@LaunchedEffect
            timeProgress = 1f - i.toFloat() / steps
        }
        shakeKey++; delay(500L); roundTimedOut = true
    }

    LaunchedEffect(shakeKey) {
        if (shakeKey == 0) return@LaunchedEffect
        shakeOffset.snapTo(0f)
        shakeOffset.animateTo(0f, animationSpec = keyframes {
            durationMillis = 400
            20f at 50; (-20f) at 100; 20f at 150; (-20f) at 200; 10f at 250; (-10f) at 300; 0f at 400
        })
    }

    MissionControlBackground {
        when (gamePhase) {
            GamePhase.INTRO -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(40.dp),
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = stringResource(R.string.title_ai_game), style = MaterialTheme.typography.displaySmall, color = MatrixGreen)
                            Spacer(Modifier.height(24.dp))
                            AISpeechBubble(videoAssetManager, isPlayingAudio, {
                                if (!isPlayingAudio) mediaPlayer?.start().also { isPlayingAudio = true }
                                else mediaPlayer?.pause().also { isPlayingAudio = false }
                            }, Modifier.size(180.dp))
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MatrixGreen.copy(alpha = 0.4f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = stringResource(R.string.ai_junior_intro_line1) + "\n\n" +
                                           stringResource(R.string.ai_junior_intro_line2) + "\n" +
                                           stringResource(R.string.ai_junior_intro_line3),
                                    modifier = Modifier.padding(24.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White
                                )
                            }
                            Spacer(Modifier.height(24.dp))
                            Button(
                                onClick = {
                                    scope.launch {
                                        mediaPlayer?.stop(); isPlayingAudio = false
                                        for (step in listOf("3", "2", "1", goLabel)) { countdownLabel = step; gamePhase = GamePhase.COUNTDOWN; delay(1000L) }
                                        gamePhase = GamePhase.GAME
                                    }
                                },
                                enabled = hasFinishedAudio,
                                modifier = Modifier.fillMaxWidth().height(64.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                            ) {
                                Text(stringResource(R.string.ai_intro_btn_start), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    HomeButton(onHome, Modifier.align(Alignment.TopEnd).padding(16.dp))
                }
            }

            GamePhase.COUNTDOWN -> {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(countdownLabel, fontSize = 180.sp, fontWeight = FontWeight.Black, color = if (countdownLabel == goLabel) BrandGreen else BrandBlue)
                }
            }

            GamePhase.GAME -> {
                Box(Modifier.fillMaxSize()) {
                    if (roundTimedOut) {
                        Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.binary_times_up), fontSize = 40.sp, color = ErrorRed, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(32.dp))
                            Button({ roundTimedOut = false; attemptKey++ }, Modifier.height(72.dp).fillMaxWidth(0.6f)) {
                                Text(stringResource(R.string.binary_continue_anyway))
                            }
                        }
                    } else if (gameWon) {
                        Column(Modifier.fillMaxSize().padding(40.dp), Arrangement.Center, Alignment.CenterHorizontally) {
                            Text(stringResource(R.string.ai_game_won_title), style = MaterialTheme.typography.displaySmall, color = MatrixGreen)
                            Spacer(Modifier.height(32.dp))
                            Button(onGameCompleted, Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(20.dp)) {
                                Text(stringResource(R.string.btn_continue), style = MaterialTheme.typography.titleLarge)
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(32.dp).graphicsLayer { translationX = shakeOffset.value },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("$correctCount / $totalRounds", style = MaterialTheme.typography.displaySmall, color = MatrixGreen)
                            Spacer(Modifier.height(16.dp))
                            LinearProgressIndicator(timeProgress, Modifier.fillMaxWidth().height(12.dp).clip(CircleShape), color = if (timeProgress > 0.3f) MatrixGreen else ErrorRed)
                            Spacer(Modifier.height(32.dp))
                            Text(stringResource(R.string.ai_game_find_intruder), style = MaterialTheme.typography.headlineSmall, color = Color.White)
                            Spacer(Modifier.height(32.dp))

                            round.items.chunked(3).forEach { rowItems ->
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
                                    rowItems.forEach { item ->
                                        EmojiCard(
                                            emoji = item,
                                            onClick = {
                                                if (item == round.items[round.intruderIndex]) {
                                                    correctCount++
                                                    if (correctCount >= totalRounds) gameWon = true else attemptKey++
                                                    playSuccessSfx(context)
                                                } else {
                                                    shakeKey++
                                                    playFailSfx(context)
                                                }
                                            }
                                        )
                                    }
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }
                    HomeButton(onHome, Modifier.align(Alignment.TopEnd).padding(16.dp))
                }
            }
        }
    }
}
