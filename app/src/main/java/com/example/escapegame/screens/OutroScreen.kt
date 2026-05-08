package com.example.escapegame.screens

import android.view.TextureView
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.escapegame.R
import com.example.escapegame.logic.VideoAsset
import com.example.escapegame.logic.VideoAssetManager
import kotlinx.coroutines.delay
import java.util.Locale

private data class SubtitleEntry(val startMs: Long, val endMs: Long, val text: String)

private fun parseTimeMs(time: String): Long {
    val (h, m, rest) = time.split(":")
    val (s, ms) = rest.split(",")
    return h.toLong() * 3_600_000 + m.toLong() * 60_000 + s.toLong() * 1_000 + ms.toLong()
}

private fun parseSrt(content: String): List<SubtitleEntry> {
    val entries = mutableListOf<SubtitleEntry>()
    val blocks = content.trim().split(Regex("""\r?\n\s*\r?\n"""))
    for (block in blocks) {
        val lines = block.trim().lines()
        if (lines.size < 3) continue
        val timeLine = lines.getOrNull(1) ?: continue
        val match = Regex("""(\d{2}:\d{2}:\d{2},\d{3}) --> (\d{2}:\d{2}:\d{2},\d{3})""")
            .find(timeLine) ?: continue
        val start = parseTimeMs(match.groupValues[1])
        val end = parseTimeMs(match.groupValues[2])
        val text = lines.drop(2).joinToString("\n")
        entries.add(SubtitleEntry(start, end, text))
    }
    return entries
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun OutroScreen(
    videoAssetManager: VideoAssetManager,
    onFinished: () -> Unit,
) {
    val context = videoAssetManager.context
    val uri = remember { videoAssetManager.getUri(VideoAsset.OUTRO) }

    val currentLanguage = remember {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (!appLocales.isEmpty) appLocales[0]?.language ?: Locale.getDefault().language
        else Locale.getDefault().language
    }

    val subtitleEntries = remember(currentLanguage) {
        val lang = when (currentLanguage) {
            "nl" -> "nl"
            "en" -> "en"
            else -> "fr"
        }
        try {
            val content = context.assets.open("subtitles/outro_$lang.srt").bufferedReader().readText()
            parseSrt(content)
        } catch (_: Exception) {
            emptyList()
        }
    }

    val player = remember {
        ExoPlayer.Builder(context).build().apply {
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

    var currentSubtitle by remember { mutableStateOf("") }

    LaunchedEffect(player, subtitleEntries) {
        while (true) {
            if (player.isPlaying) {
                val pos = player.currentPosition
                val entry = subtitleEntries.find { pos in it.startMs..it.endMs }
                val newText = entry?.text ?: ""
                if (currentSubtitle != newText) {
                    currentSubtitle = newText
                }
            }
            delay(16)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).also { player.setVideoTextureView(it) }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (currentSubtitle.isNotEmpty()) {
            Text(
                text = currentSubtitle,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp, start = 32.dp, end = 32.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        TextButton(
            onClick = onFinished,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(R.string.btn_continue),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}
