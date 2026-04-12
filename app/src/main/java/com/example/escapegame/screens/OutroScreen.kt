package com.example.escapegame.screens

import android.view.TextureView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.escapegame.logic.VideoAsset
import com.example.escapegame.logic.VideoAssetManager

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun OutroScreen(
    videoAssetManager: VideoAssetManager,
    onFinished: () -> Unit,
) {
    val uri = remember { videoAssetManager.getUri(VideoAsset.OUTRO) }

    val player = remember {
        ExoPlayer.Builder(videoAssetManager.context).build().apply {
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) onFinished()
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).also { player.setVideoTextureView(it) }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
