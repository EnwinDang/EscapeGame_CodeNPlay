package com.example.escapegame.logic

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.net.URL

/**
 * Checks a remote manifest for newer video versions and downloads them
 * silently in the background whenever the device is online.
 *
 * - Downloads go to `filesDir/videos/` using an atomic temp-then-rename strategy
 *   so a failed or interrupted download never leaves a corrupted file.
 * - Downloaded versions are tracked in SharedPreferences; a file is only
 *   re-downloaded when the manifest reports a higher version number.
 * - All failures are swallowed — bundled assets always serve as the offline fallback.
 *
 * Enable by setting [VideoConfig.MANIFEST_URL] to your hosted manifest URL.
 * Leave it empty to skip all network activity entirely.
 */
class VideoUpdateManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("video_versions", Context.MODE_PRIVATE)

    suspend fun checkAndUpdate() {
        if (VideoConfig.MANIFEST_URL.isEmpty()) return
        if (!isNetworkAvailable()) return

        try {
            val videosNode = fetchManifest()
            for (asset in VideoAsset.entries) {
                val remote = videosNode.optJSONObject(asset.key) ?: continue
                val remoteVersion = remote.optInt("version", 0)
                val localVersion = prefs.getInt(asset.key, 0)
                if (remoteVersion > localVersion) {
                    val url = remote.optString("url").takeIf { it.isNotEmpty() } ?: continue
                    downloadVideo(asset, url, remoteVersion)
                }
            }
        } catch (_: Exception) {
            // Silent — bundled assets remain the offline fallback
        }
    }

    private suspend fun fetchManifest(): JSONObject = withContext(Dispatchers.IO) {
        JSONObject(URL(VideoConfig.MANIFEST_URL).readText()).getJSONObject("videos")
    }

    private suspend fun downloadVideo(asset: VideoAsset, url: String, version: Int) =
        withContext(Dispatchers.IO) {
            val dir = File(context.filesDir, "videos").also { it.mkdirs() }
            val tempFile = File(dir, "${asset.key}.tmp")
            val finalFile = File(dir, asset.fileName)
            try {
                URL(url).openStream().use { input ->
                    tempFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                // Atomic swap — never expose a partial download to VideoAssetManager
                tempFile.renameTo(finalFile)
                prefs.edit().putInt(asset.key, version).apply()
            } catch (e: Exception) {
                tempFile.delete()
                throw e
            }
        }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
