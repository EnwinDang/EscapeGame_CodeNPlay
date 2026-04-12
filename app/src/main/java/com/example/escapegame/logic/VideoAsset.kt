package com.example.escapegame.logic

/**
 * Enumerates every video file used in the app.
 *
 * Adding a new video requires only a new entry here — everything else
 * (asset resolution, remote updates) picks it up automatically.
 */
enum class VideoAsset(val fileName: String) {
    INTRO("intro.mp4"),
    OUTRO("outro.mp4"),
    AI_SPEECH_BUBBLE("ai_speech_bubble.mp4");

    /** Key used in the remote manifest JSON and in SharedPreferences version tracking. */
    val key: String get() = fileName.removeSuffix(".mp4")
}
