package com.example.escapegame.logic

/**
 * Central configuration for remote video delivery.
 *
 * To enable remote video updates, point [MANIFEST_URL] at a JSON file with this shape:
 *
 * ```json
 * {
 *   "videos": {
 *     "intro":            { "version": 2, "url": "https://your-host.com/videos/intro_v2.mp4" },
 *     "outro":            { "version": 1, "url": "https://your-host.com/videos/outro_v1.mp4" },
 *     "ai_speech_bubble": { "version": 1, "url": "https://your-host.com/videos/ai_speech_bubble_v1.mp4" }
 *   }
 * }
 * ```
 *
 * Bump `version` whenever you replace a video file. The app downloads the new file
 * silently in the background the next time it launches with internet access.
 * Bundled assets remain the fallback when offline.
 *
 * Leave [MANIFEST_URL] empty to run in fully-offline / bundled-only mode.
 */
object VideoConfig {
    const val MANIFEST_URL = ""
}
