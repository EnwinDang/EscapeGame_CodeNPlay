package com.example.escapegame.screens

import android.media.MediaPlayer
import com.example.escapegame.logic.playSuccessSfx
import com.example.escapegame.logic.playFailSfx
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.logic.VideoAssetManager
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandYellow
import com.example.escapegame.theme.ErrorRed
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground

@Composable
fun ExternalGameScreen(
    stepNumber: Int,
    title: String,
    instructions: String,
    correctCode: String,
    onCodeCorrect: () -> Unit,
    onHome: () -> Unit,
    videoAssetManager: VideoAssetManager? = null,
    audioPrefix: String = "",
) {
    BackHandler(enabled = true) { /* back disabled during game */ }

    val isPreview = LocalInspectionMode.current
    val context = LocalContext.current
    var isBubblePlaying by remember { mutableStateOf(true) }

    val locale = if (isPreview) "fr"
                 else AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"

    val hasBubble = videoAssetManager != null && audioPrefix.isNotBlank()

    val mediaPlayer: MediaPlayer? = remember {
        if (isPreview || !hasBubble) null
        else try {
            MediaPlayer().apply {
                try {
                    val afd = context.assets.openFd("audio/${audioPrefix}_$locale.mp3")
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
                20f at 50; (-20f) at 100; 20f at 150; (-20f) at 200
                10f at 250; (-10f) at 300; 0f at 400
            }
        )
    }

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 24.dp)
                    .graphicsLayer { translationX = shakeOffset.value },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mission label
                Text(
                    text = stringResource(R.string.external_step_label, stepNumber, title),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BrandBlue,
                    letterSpacing = 1.5.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Title
                Text(
                    text = title,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    color = BrandYellow,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )

                // Scratch logo (only on Mission 2)
                if (stepNumber == 2) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Image(
                        painter = painterResource(id = R.drawable.scratch),
                        contentDescription = "Scratch Logo",
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Speech bubble + instructions row (or plain box without bubble)
                if (hasBubble) {
                    Row(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AISpeechBubble(
                            videoAssetManager = videoAssetManager!!,
                            isPlaying = isBubblePlaying,
                            onPlay = {
                                if (!isBubblePlaying) {
                                    mediaPlayer?.start(); isBubblePlaying = true
                                } else {
                                    mediaPlayer?.pause(); isBubblePlaying = false
                                }
                            },
                            modifier = Modifier.size(120.dp)
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.07f))
                                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = instructions,
                                fontSize = 15.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 22.sp
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.07f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = instructions,
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Lock / code entry
                if (locked) {
                    IconButton(
                        onClick = { locked = false },
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MatrixGreen.copy(alpha = 0.1f))
                            .border(1.dp, MatrixGreen.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Unlock to enter code",
                            modifier = Modifier.size(56.dp),
                            tint = MatrixGreen
                        )
                    }
                } else {
                    OutlinedTextField(
                        value = enteredCode,
                        onValueChange = {
                            enteredCode = it
                            showError = false
                        },
                        label = {
                            Text(
                                stringResource(R.string.external_enter_code_label),
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(0.6f),
                        singleLine = true,
                        textStyle = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            color = Color.White
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MatrixGreen,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.25f),
                            focusedLabelColor = MatrixGreen,
                            focusedContainerColor = Color(0xFF060F08),
                            unfocusedContainerColor = Color(0xFF060F08),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Submit button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(56.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MatrixGreen)
                            .clickable {
                                if (enteredCode.trim().uppercase() == correctCode) {
                                    playSuccessSfx(context)
                                    onCodeCorrect()
                                } else {
                                    showError = true
                                    shakeKey++
                                    playFailSfx(context)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LockOpen,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = stringResource(R.string.btn_submit),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    if (showError) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.external_wrong_code),
                            color = ErrorRed,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
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
