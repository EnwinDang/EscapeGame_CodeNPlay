package com.example.escapegame.screens

import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.escapegame.R
import com.example.escapegame.logic.VideoAssetManager
import com.example.escapegame.theme.ErrorRed
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground

// ── Game flow ──────────────────────────────────────────────────────────────────
//
//  INTRO → FIRST_RUN (A+B + arrows + press A) → CODE_INPUT_1 (any code)
//       → BAD_PREDICTION (error: trained on wrong data)
//       → RECALIBRATE (press B, perform movements, wait for DONE)
//       → SECOND_RUN (A+B again + arrows + press A) → CODE_INPUT_2 (correct code)
//       → SUCCESS
//
private enum class TeensAiPhase {
    INTRO, FIRST_RUN, CODE_INPUT_1,
    BAD_PREDICTION,
    RECALIBRATE, UPLOADING,
    SECOND_RUN, CODE_INPUT_2,
    SUCCESS
}

@Composable
fun AiValidationScreen(
    correctCode: String,
    onGameCompleted: () -> Unit,
    onHome: () -> Unit,
    videoAssetManager: VideoAssetManager? = null,
) {
    BackHandler(enabled = true) {}

    val isPreview = LocalInspectionMode.current
    val context   = LocalContext.current
    var isBubblePlaying    by remember { mutableStateOf(true) }
    var hasFinishedAudio   by remember { mutableStateOf(videoAssetManager == null) }

    val locale = if (isPreview) "fr"
                 else AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"

    val mediaPlayer: MediaPlayer? = remember {
        if (isPreview || videoAssetManager == null) null
        else try {
            MediaPlayer().apply {
                try {
                    val afd = context.assets.openFd("audio/ai_senior_$locale.mp3")
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                } catch (_: Exception) {
                    try {
                        val afd = context.assets.openFd("audio/test.mp3")
                        setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    } catch (_: Exception) { }
                }
                try { prepare(); start() } catch (_: Exception) { }
                setOnCompletionListener { isBubblePlaying = false; hasFinishedAudio = true }
            }
        } catch (_: Exception) { null }
    }

    DisposableEffect(Unit) { onDispose { mediaPlayer?.release() } }

    var phase by remember { mutableStateOf(TeensAiPhase.INTRO) }
    var phaseAudioPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // Play a different audio clip when BAD_PREDICTION or RECALIBRATE starts.
    // Silently skipped if the file doesn't exist yet.
    androidx.compose.runtime.LaunchedEffect(phase) {
        phaseAudioPlayer?.release()
        phaseAudioPlayer = null
        if (isPreview || videoAssetManager == null) return@LaunchedEffect
        val filename = when (phase) {
            TeensAiPhase.BAD_PREDICTION -> "ai_senior_mismatch_$locale.mp3"
            TeensAiPhase.RECALIBRATE    -> "ai_senior_recalibrate_$locale.mp3"
            else                        -> return@LaunchedEffect
        }
        try {
            phaseAudioPlayer = MediaPlayer().apply {
                val afd = context.assets.openFd("audio/$filename")
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
            }
        } catch (_: Exception) { }
    }

    DisposableEffect("phaseAudio") { onDispose { phaseAudioPlayer?.release() } }
    var firstCode by remember { mutableStateOf("") }
    var secondCode by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val shakeOffset = remember { Animatable(0f) }
    var shakeKey by remember { mutableIntStateOf(0) }

    androidx.compose.runtime.LaunchedEffect(shakeKey) {
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

    val statusColor = when (phase) {
        TeensAiPhase.BAD_PREDICTION -> ErrorRed
        TeensAiPhase.SUCCESS        -> MatrixGreen
        else                        -> MaterialTheme.colorScheme.primary
    }

    val statusLabel = when (phase) {
        TeensAiPhase.INTRO,
        TeensAiPhase.FIRST_RUN,
        TeensAiPhase.CODE_INPUT_1   -> stringResource(R.string.ai_teens_status_phase1)
        TeensAiPhase.BAD_PREDICTION -> stringResource(R.string.ai_teens_status_mismatch)
        TeensAiPhase.RECALIBRATE,
        TeensAiPhase.UPLOADING      -> stringResource(R.string.ai_teens_status_recalibrating)
        TeensAiPhase.SECOND_RUN,
        TeensAiPhase.CODE_INPUT_2   -> stringResource(R.string.ai_teens_status_phase2)
        TeensAiPhase.SUCCESS        -> stringResource(R.string.ai_teens_status_restored)
    }

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {

            // Status indicator — top-left, always visible
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 20.dp, top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // All phase content — padded below the top chrome
            AnimatedContent(
                targetState = phase,
                modifier = Modifier.fillMaxSize().padding(top = 52.dp),
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "ai_phase"
            ) { currentPhase ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { translationX = shakeOffset.value }
                ) {
                    when (currentPhase) {

                        // ── INTRO ──────────────────────────────────────────────
                        TeensAiPhase.INTRO -> CenteredColumn {
                            Text(
                                text = stringResource(R.string.ai_teens_intro_title),
                                style = MaterialTheme.typography.headlineMedium,
                                color = MatrixGreen,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            if (videoAssetManager != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                                        modifier = Modifier.size(120.dp)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                Color.White.copy(alpha = 0.07f),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .border(
                                                1.dp,
                                                Color.White.copy(alpha = 0.12f),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .padding(16.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.ai_teens_intro_task),
                                            fontSize = 18.sp,
                                            color = Color.White.copy(alpha = 0.85f),
                                            lineHeight = 26.sp
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.ai_teens_intro_task),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(Modifier.height(40.dp))
                            ActionButton(
                                label = stringResource(R.string.ai_teens_btn_start),
                                containerColor = MatrixGreen,
                                contentColor = Color.Black,
                                enabled = true,
                                onClick = { phase = TeensAiPhase.FIRST_RUN }
                            )
                        }

                        // ── FIRST RUN — instructions + inline code entry ──────
                        TeensAiPhase.FIRST_RUN -> CenteredColumn {
                            PhaseTitle(stringResource(R.string.ai_teens_first_run_title))
                            Spacer(Modifier.height(28.dp))
                            NumberedStepPanel(
                                steps = stringResource(R.string.ai_teens_first_run_body)
                                    .split("\n").map { it.removePrefix("→ ") },
                                borderColor = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(28.dp))
                            CodeField(
                                value = firstCode,
                                onValueChange = { firstCode = it; showError = false }
                            )
                            Spacer(Modifier.height(16.dp))
                            ActionButton(
                                label = stringResource(R.string.ai_teens_btn_enter_code),
                                onClick = {
                                    when (firstCode.trim().uppercase()) {
                                        "DATA"  -> phase = TeensAiPhase.BAD_PREDICTION
                                        "INPUT" -> {
                                            errorMessage = context.getString(R.string.ai_teens_input_error)
                                            showError = true
                                            shakeKey++
                                        }
                                        else -> {
                                            errorMessage = context.getString(R.string.external_wrong_code)
                                            showError = true
                                            shakeKey++
                                        }
                                    }
                                }
                            )
                            if (showError) {
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = errorMessage,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // CODE_INPUT_1 is no longer a separate phase — entry is embedded in FIRST_RUN
                        TeensAiPhase.CODE_INPUT_1 -> { }

                        // ── BAD PREDICTION — warning + storyline + recalibrate button ──
                        TeensAiPhase.BAD_PREDICTION -> {
                            val pulseTransition = rememberInfiniteTransition(label = "pulse")
                            val pulseAlpha by pulseTransition.animateFloat(
                                initialValue = 0.5f,
                                targetValue = 1f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(600),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "pulse_alpha"
                            )

                            CenteredColumn(horizontalPadding = 32.dp) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Warning,
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp),
                                        tint = ErrorRed.copy(alpha = pulseAlpha)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        text = stringResource(R.string.ai_teens_mismatch_title),
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = ErrorRed.copy(alpha = pulseAlpha),
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    )
                                }
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    text = stringResource(R.string.ai_teens_mismatch_body),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(40.dp))
                                ActionButton(
                                    label = stringResource(R.string.ai_teens_btn_diagnose),
                                    containerColor = ErrorRed,
                                    contentColor = Color.White,
                                    onClick = { phase = TeensAiPhase.RECALIBRATE }
                                )
                            }
                        }

                        // ── RECALIBRATE — physical micro:bite training ──────────
                        TeensAiPhase.RECALIBRATE -> CenteredColumn {
                            PhaseTitle(stringResource(R.string.ai_teens_recalibrate_title))
                            Spacer(Modifier.height(28.dp))
                            NumberedStepPanel(
                                steps = stringResource(R.string.ai_teens_recalibrate_body)
                                    .split("\n").map { it.removePrefix("→ ") },
                                borderColor = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(36.dp))
                            ActionButton(
                                label = stringResource(R.string.ai_teens_recalibrate_done_btn),
                                onClick = { phase = TeensAiPhase.UPLOADING }
                            )
                        }

                        // ── UPLOADING — validates recorded data into the system ──
                        TeensAiPhase.UPLOADING -> {
                            val progressAnim = remember { Animatable(0f) }

                            androidx.compose.runtime.LaunchedEffect(Unit) {
                                progressAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(3000, easing = LinearEasing)
                                )
                                phase = TeensAiPhase.SECOND_RUN
                            }

                            val progress = progressAnim.value
                            val statusMsg = when {
                                progress < 0.30f -> stringResource(R.string.ai_teens_upload_status_1)
                                progress < 0.60f -> stringResource(R.string.ai_teens_upload_status_2)
                                progress < 0.90f -> stringResource(R.string.ai_teens_upload_status_3)
                                else             -> stringResource(R.string.ai_teens_upload_status_4)
                            }

                            CenteredColumn {
                                PhaseTitle(stringResource(R.string.ai_teens_uploading_title))
                                Spacer(Modifier.height(40.dp))
                                SystemPanel(borderColor = MatrixGreen) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = statusMsg,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MatrixGreen
                                        )
                                        Text(
                                            text = "${(progress * 100).toInt()}%",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MatrixGreen,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier.fillMaxWidth().height(8.dp),
                                        color = MatrixGreen,
                                        trackColor = MatrixGreen.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }

                        // ── SECOND RUN — rerun after recalibration ─────────────
                        TeensAiPhase.SECOND_RUN -> CenteredColumn {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp),
                                    tint = MatrixGreen
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.ai_teens_retrain_success),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MatrixGreen,
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(Modifier.height(12.dp))
                            PhaseTitle(stringResource(R.string.ai_teens_second_run_title))
                            Spacer(Modifier.height(28.dp))
                            NumberedStepPanel(
                                steps = stringResource(R.string.ai_teens_first_run_body)
                                    .split("\n").map { it.removePrefix("→ ") },
                                borderColor = MatrixGreen
                            )
                            Spacer(Modifier.height(36.dp))
                            CodeField(
                                value = secondCode,
                                onValueChange = { secondCode = it; showError = false }
                            )
                            Spacer(Modifier.height(24.dp))
                            ActionButton(
                                label = stringResource(R.string.btn_submit),
                                onClick = {
                                    when {
                                        secondCode.trim().uppercase() == correctCode -> phase = TeensAiPhase.SUCCESS
                                        secondCode.trim().uppercase() == "DATA" -> {
                                            errorMessage = context.getString(R.string.ai_teens_too_many_errors)
                                            showError = true
                                            shakeKey++
                                        }
                                        else -> {
                                            errorMessage = context.getString(R.string.external_wrong_code)
                                            showError = true
                                            shakeKey++
                                        }
                                    }
                                }
                            )
                            if (showError) {
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = errorMessage,
                                    color = ErrorRed,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                if (secondCode.trim().uppercase() == "DATA") {
                                    Spacer(Modifier.height(12.dp))
                                    ActionButton(
                                        label = stringResource(R.string.ai_teens_btn_retrain),
                                        containerColor = ErrorRed,
                                        contentColor = Color.White,
                                        onClick = {
                                            secondCode = ""
                                            showError = false
                                            phase = TeensAiPhase.RECALIBRATE
                                        }
                                    )
                                }
                            }
                        }

                        // CODE_INPUT_2 is no longer a separate phase — code entry is embedded in SECOND_RUN
                        TeensAiPhase.CODE_INPUT_2 -> { }

                        // ── SUCCESS ────────────────────────────────────────────
                        TeensAiPhase.SUCCESS -> CenteredColumn {
                            Text(
                                text = stringResource(R.string.ai_teens_success_title),
                                style = MaterialTheme.typography.headlineLarge,
                                color = MatrixGreen,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(24.dp))
                            Text(
                                text = stringResource(R.string.ai_teens_success_body),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(40.dp))
                            ActionButton(
                                label = stringResource(R.string.btn_continue),
                                containerColor = MatrixGreen,
                                contentColor = Color.Black,
                                onClick = onGameCompleted
                            )
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

// ── Shared UI helpers ──────────────────────────────────────────────────────────

/** Centered full-screen column with standard horizontal padding. */
@Composable
private fun CenteredColumn(
    horizontalPadding: androidx.compose.ui.unit.Dp = 48.dp,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding, vertical = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = { content() }
    )
}

@Composable
private fun PhaseTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp
    )
}

@Composable
private fun SystemPanel(
    borderColor: Color,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
            .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(20.dp)
    ) {
        content()
    }
}

@Composable
private fun NumberedStepPanel(steps: List<String>, borderColor: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        steps.forEachIndexed { index, step ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(borderColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = borderColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyMedium,
                    color = borderColor,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ComparisonRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CodeField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Code") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MatrixGreen,
            unfocusedBorderColor = MatrixGreen.copy(alpha = 0.5f),
            focusedLabelColor = MatrixGreen
        )
    )
}

@Composable
private fun ActionButton(
    label: String,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = Color.Black,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.3f),
            disabledContentColor = contentColor.copy(alpha = 0.4f)
        )
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}
