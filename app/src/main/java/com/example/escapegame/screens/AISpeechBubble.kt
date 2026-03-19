package com.example.escapegame.screens

import android.net.Uri
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.escapegame.R
import kotlinx.coroutines.launch

private val BubbleBlue = Color(0xFF3AAFE8)

@Composable
fun AISpeechBubble(
    modifier: Modifier = Modifier,
    // TODO: pass onReplayVoice: () -> Unit here when voice is implemented
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse("asset:///videos/ai_speech_bubble.mp4")))
            repeatMode = Player.REPEAT_MODE_ONE
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    // Slow breathing ring — active while playing
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Flash on tap — briefly brightens the ring
    val tapFlash = remember { Animatable(0f) }

    Box(
        modifier = modifier
            .size(90.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                scope.launch {
                    tapFlash.snapTo(1f)
                    tapFlash.animateTo(0f, animationSpec = tween(400))
                }
                player.seekTo(0)
                player.play()
                // TODO: onReplayVoice()
            }
    ) {
        // Pulsing ring
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = 2.5.dp.toPx()
            val radius = size.minDimension / 2f - strokePx / 2f
            // Base breathing ring
            drawCircle(
                color = BubbleBlue,
                radius = radius,
                style = Stroke(width = strokePx),
                alpha = pulseAlpha
            )
            // Tap flash ring — same ring, brighter
            if (tapFlash.value > 0f) {
                drawCircle(
                    color = Color.White,
                    radius = radius,
                    style = Stroke(width = strokePx * 2),
                    alpha = tapFlash.value * 0.8f
                )
            }
        }

        // Bubble video — circular crop
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .clip(CircleShape)
        )

        // Subtle replay hint icon — bottom-right of bubble
        Icon(
            painter = painterResource(R.drawable.ic_replay),
            contentDescription = "Replay voice",
            tint = Color.White.copy(alpha = 0.55f),
            modifier = Modifier
                .size(18.dp)
                .align(Alignment.BottomEnd)
                .padding(end = 2.dp, bottom = 2.dp)
        )
    }
}
