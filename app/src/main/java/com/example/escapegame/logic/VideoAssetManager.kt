package com.example.escapegame.logic

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Resolves a [VideoAsset] to a playable [Uri] for ExoPlayer.
 *
 * Resolution order (first match wins):
 *  1. `filesDir/videos/<name>.mp4` — downloaded by [VideoUpdateManager] when a newer
 *     version is available on the server.
 *  2. `assets/videos/<name>.mp4` — bundled with the APK; always available offline.
 *
 * This class is stateless and cheap to instantiate from any composable.
 */
class VideoAssetManager(val context: Context) {

    fun getUri(asset: VideoAsset): Uri {
        val cached = cachedFile(asset)
        return if (cached.exists()) Uri.fromFile(cached)
        else Uri.parse("asset:///videos/${asset.fileName}")
    }

    /** Returns the path where [VideoUpdateManager] stores a downloaded video. */
    fun cachedFile(asset: VideoAsset): File =
        File(context.filesDir, "videos/${asset.fileName}")
}
