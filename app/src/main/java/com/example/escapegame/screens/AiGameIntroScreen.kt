package com.example.escapegame.screens

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.logic.VideoAssetManager
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandGreen
import com.example.escapegame.theme.ErrorRed
import com.example.escapegame.theme.MissionControlBackground
import com.example.escapegame.theme.YellowDark
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class IntroStoryElement(
    val icon: ImageVector,
    val iconColor: Color,
    val titleRes: Int,
    val bodyRes: Int,
    val startTimeMs: Int,
)

private val introStoryElements = listOf(
    IntroStoryElement(
        icon       = Icons.Filled.Warning,
        iconColor  = ErrorRed,
        titleRes   = R.string.ai_intro_alarm_title,
        bodyRes    = R.string.ai_intro_alarm_body,
        startTimeMs = 0
    ),
    IntroStoryElement(
        icon       = Icons.Filled.BugReport,
        iconColor  = YellowDark,
        titleRes   = R.string.ai_intro_threat_title,
        bodyRes    = R.string.ai_intro_threat_body,
        startTimeMs = 8000
    ),
    IntroStoryElement(
        icon       = Icons.Filled.SmartToy,
        iconColor  = BrandBlue,
        titleRes   = R.string.ai_intro_mission_title,
        bodyRes    = R.string.ai_intro_mission_body,
        startTimeMs = 20000
    ),
)

@Composable
fun AiGameIntroScreen(
    videoAssetManager: VideoAssetManager,
    onReady: () -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) {}

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isPlaying by remember { mutableStateOf(false) }
    var hasFinishedAudio by remember { mutableStateOf(false) }
    var currentProgressMs by remember { mutableIntStateOf(0) }
    var countdownLabel by remember { mutableStateOf<String?>(null) }
    val goLabel = stringResource(R.string.ai_game_intro_go)

    val locale = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
    val audioPath = "audio/intro_$locale.mp3"

    val mediaPlayer = remember {
        MediaPlayer().apply {
            try {
                val afd = context.assets.openFd(audioPath)
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
            } catch (e: Exception) {
                // Als audio faalt, markeren we het als klaar zodat de knop werkt
                hasFinishedAudio = true
            }
            setOnCompletionListener { 
                isPlaying = false
                hasFinishedAudio = true
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose { mediaPlayer.release() }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                try {
                    currentProgressMs = mediaPlayer.currentPosition
                } catch (e: Exception) { isPlaying = false }
                delay(100L)
            }
        }
    }

    val currentElement = introStoryElements.lastOrNull { currentProgressMs >= it.startTimeMs } ?: introStoryElements[0]

    MissionControlBackground {
        AnimatedContent(
            targetState = countdownLabel,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.6f)) togetherWith
                        (fadeOut() + scaleOut(targetScale = 1.3f))
            },
            label = "countdown_anim"
        ) { label ->
            if (label == null) {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomeButton(
                        onHome = onHome,
                        modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                    )

                    AISpeechBubble(
                        videoAssetManager = videoAssetManager,
                        isPlaying = isPlaying,
                        onPlay = {
                            if (!isPlaying) {
                                try { mediaPlayer.start(); isPlaying = true } catch(e: Exception) { hasFinishedAudio = true }
                            } else {
                                try { mediaPlayer.pause(); isPlaying = false } catch(e: Exception) {}
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 20.dp)
                            .size(160.dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(horizontal = 40.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AnimatedContent(
                            targetState = currentElement,
                            transitionSpec = { (fadeIn() + scaleIn()) togetherWith (fadeOut() + scaleOut()) },
                            label = "element_anim"
                        ) { element ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(CircleShape)
                                        .background(element.iconColor.copy(alpha = 0.15f))
                                        .border(4.dp, element.iconColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(element.icon, null, Modifier.size(100.dp), element.iconColor)
                                }
                                Spacer(Modifier.height(32.dp))
                                Text(stringResource(element.titleRes), style = MaterialTheme.typography.displaySmall, color = element.iconColor, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                                Spacer(Modifier.height(16.dp))
                                Text(stringResource(element.bodyRes), style = MaterialTheme.typography.headlineSmall, color = Color.White, textAlign = TextAlign.Center, lineHeight = 34.sp)
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(32.dp)
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    try { mediaPlayer.stop() } catch(e: Exception) {}
                                    isPlaying = false
                                    for (step in listOf("3", "2", "1", goLabel)) {
                                        countdownLabel = step
                                        delay(1000L)
                                    }
                                    onReady()
                                }
                            },
                            enabled = true, // ALTIJD AAN ZETTEN OM VASTLOPEN TE VOORKOMEN
                            modifier = Modifier.fillMaxWidth().height(80.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                        ) {
                            Text(stringResource(R.string.ai_intro_btn_start), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        }
                    }
                }
            } else {
                CountdownContent(label = label, isGo = label == goLabel)
            }
        }
    }
}

@Composable
private fun CountdownContent(label: String, isGo: Boolean) {
    val textColor = if (isGo) BrandGreen else BrandBlue
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(label, fontSize = 180.sp, fontWeight = FontWeight.Black, color = textColor, textAlign = TextAlign.Center)
    }
}
