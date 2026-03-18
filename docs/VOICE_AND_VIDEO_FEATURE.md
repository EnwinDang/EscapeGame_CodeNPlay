# AI Voice & Video Feature Plan

## Overview

Add pre-generated AI voice narration and video playback to the CodeNPlay escape room app. All audio and video files are bundled in the app so it works **fully offline** at events — no internet required.

---

## Architecture

The voice system sits behind a simple interface so the provider can be swapped without touching any screen code.

```
Screen
  └── VoicePlayer (interface)
        └── BundledVoicePlayer  ← implementation (today)
        └── LiveApiVoicePlayer  ← future swap-in if needed
```

The video player replaces the current placeholder in `VideoScreen.kt` using **AndroidX Media3 (ExoPlayer)**, which is the modern standard for Android video playback.

---

## File Structure

```
app/src/main/
├── assets/
│   ├── audio/
│   │   ├── en/
│   │   │   ├── mission1_briefing.mp3
│   │   │   ├── mission2_briefing.mp3
│   │   │   ├── mission3_briefing.mp3
│   │   │   ├── mission4_briefing.mp3
│   │   │   └── congratulations.mp3
│   │   ├── nl/                   ← same files in Dutch
│   │   └── fr/                   ← same files in French
│   └── video/
│       └── intro_briefing.mp4    ← company-provided video
├── java/com/example/escapegame/
│   ├── voice/
│   │   ├── VoicePlayer.kt        ← interface
│   │   └── BundledVoicePlayer.kt ← plays from assets/audio/
│   └── screens/
│       └── VideoScreen.kt        ← updated with real video player
```

---

## Audio: AI Voice Narration

### Where voice plays

| Screen | Audio file | Description |
|---|---|---|
| VideoScreen | `mission1_briefing` | Story intro narration |
| BinaryGameScreen | `mission1_briefing` | Binary puzzle briefing |
| ExternalGameScreen (Scratch) | `mission2_briefing` | Scratch mission intro |
| ExternalGameScreen (AI) | `mission3_briefing` | AI mission intro |
| ExternalGameScreen (Robot) | `mission4_briefing` | Robot mission intro |
| CongratulationsScreen | `congratulations` | End-game narration |

### Generating the audio files

Use **ElevenLabs** or **OpenAI TTS** to pre-generate MP3s for each language. Scripts can be run once and the output files placed in `assets/audio/`.

**Recommended ElevenLabs settings:**
- Model: `eleven_multilingual_v2`
- Voice: pick one consistent character voice for all missions
- Output format: `mp3_44100_128`

**Recommended OpenAI TTS settings:**
- Model: `tts-1-hd`
- Voice: `nova` (friendly) or `onyx` (authoritative)
- Format: `mp3`

Scripts for generating audio go in `scripts/generate_audio/` (not part of the app build).

### VoicePlayer interface

```kotlin
// voice/VoicePlayer.kt
interface VoicePlayer {
    fun play(audioKey: String, language: String)
    fun stop()
    fun release()
}
```

### BundledVoicePlayer implementation

```kotlin
// voice/BundledVoicePlayer.kt
class BundledVoicePlayer(private val context: Context) : VoicePlayer {

    private var mediaPlayer: MediaPlayer? = null

    override fun play(audioKey: String, language: String) {
        stop()
        val path = "audio/$language/$audioKey.mp3"
        val afd = context.assets.openFd(path)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
        }
    }

    override fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun release() = stop()
}
```

### Hooking into screens (example)

```kotlin
// In BinaryGameScreen or any mission screen:
val voicePlayer = remember { BundledVoicePlayer(context) }

LaunchedEffect(Unit) {
    voicePlayer.play("mission1_briefing", currentLanguage)
}

DisposableEffect(Unit) {
    onDispose { voicePlayer.release() }
}
```

### Swapping the provider later

To switch from bundled files to a live API (e.g. if content changes frequently):

1. Create `LiveApiVoicePlayer : VoicePlayer` that calls the API and streams audio
2. Replace `BundledVoicePlayer(context)` with `LiveApiVoicePlayer(context, apiKey)` in each screen
3. No other changes needed

---

## Video: Company Intro Briefing

### What changes in VideoScreen.kt

The current `VideoScreen.kt` has a placeholder card where the video should be. This gets replaced with an actual video player using **AndroidX Media3 (ExoPlayer)**.

The video auto-plays when the screen opens. The "Continue" button appears after the video ends (or can be skipped after a few seconds).

### Dependency to add in `build.gradle.kts`

```kotlin
implementation("androidx.media3:media3-exoplayer:1.3.1")
implementation("androidx.media3:media3-ui:1.3.1")
```

### Updated VideoScreen

```kotlin
@Composable
fun VideoScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    var videoFinished by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val uri = Uri.parse("asset:///video/intro_briefing.mp4")
            setMediaItem(MediaItem.fromUri(uri))
            prepare()
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) videoFinished = true
                }
            })
        }
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    MissionControlBackground {
        Column(
            modifier = Modifier.fillMaxSize().padding(40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.video_title),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Video player
            AndroidView(
                factory = { PlayerView(it).apply { player = exoPlayer } },
                modifier = Modifier.fillMaxWidth().height(300.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Continue button appears after video ends
            if (videoFinished) {
                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().height(60.dp)
                ) {
                    Text(stringResource(R.string.btn_continue), style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
```

### Placing the company video

Drop the MP4 file here:

```
app/src/main/assets/video/intro_briefing.mp4
```

No code changes needed — just replace the file.

---

## Implementation Checklist

### Setup
- [ ] Add Media3 ExoPlayer dependency to `build.gradle.kts`
- [ ] Create `app/src/main/assets/audio/` directory structure (en/nl/fr)
- [ ] Create `app/src/main/assets/video/` directory

### Audio
- [ ] Write the narration scripts for each mission (EN, NL, FR)
- [ ] Generate MP3 files using ElevenLabs or OpenAI TTS
- [ ] Place MP3s in `assets/audio/{lang}/`
- [ ] Create `VoicePlayer.kt` interface
- [ ] Create `BundledVoicePlayer.kt` implementation
- [ ] Add voice playback to each mission screen

### Video
- [ ] Receive company video file
- [ ] Place as `assets/video/intro_briefing.mp4`
- [ ] Update `VideoScreen.kt` with ExoPlayer implementation

### Testing
- [ ] Test all 3 languages play the correct audio
- [ ] Test video plays and Continue button appears after video ends
- [ ] Test offline (airplane mode) — everything should still work
- [ ] Test on target tablet hardware

---

## Notes

- All content is bundled — the app works with **no internet connection**
- To update audio content, regenerate MP3s and rebuild the app
- Video file should be **compressed for tablet** (720p is enough, keeps APK size reasonable)
- If the APK gets too large due to assets, consider using Android App Bundle or a download-on-first-run approach for the video only
