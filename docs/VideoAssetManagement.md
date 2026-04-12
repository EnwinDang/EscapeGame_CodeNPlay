# Video Asset Management

## Overview

Videos are resolved through a three-layer system that keeps the app working offline while allowing remote updates when internet is available.

```
Priority 1: filesDir/videos/        ← downloaded update (newest)
Priority 2: assets/videos/          ← bundled with APK (always available)
Priority 3: remote manifest         ← checked silently on launch when online
```

---

## Key Files

| File | Responsibility |
|---|---|
| `logic/VideoAsset.kt` | Enum of every video file in the app |
| `logic/VideoConfig.kt` | Manifest URL — the single place to configure remote delivery |
| `logic/VideoAssetManager.kt` | Resolves a `VideoAsset` to a playable `Uri` |
| `logic/VideoUpdateManager.kt` | Checks the manifest and downloads newer versions in the background |

---

## Adding a New Video

1. Add a new entry to `VideoAsset.kt`:

   ```kotlin
   enum class VideoAsset(val fileName: String) {
       INTRO("intro.mp4"),
       OUTRO("outro.mp4"),
       AI_SPEECH_BUBBLE("ai_speech_bubble.mp4"),
       MY_NEW_VIDEO("my_new_video.mp4")   // ← add here
   }
   ```

2. Place the bundled version of the file in `app/src/main/assets/videos/`.

3. Use it in any composable via the `videoAssetManager` passed from `NavGraph`:

   ```kotlin
   val uri = remember { videoAssetManager.getUri(VideoAsset.MY_NEW_VIDEO) }
   ```

`VideoUpdateManager` picks up the new enum entry automatically — no other changes needed.

---

## Replacing a Video Without a New APK

When you have a server set up (see [Enabling Remote Updates](#enabling-remote-updates)), you can push a new video to all devices without shipping a new APK.

1. Compress and host the new video file at a stable URL.
2. Bump the `version` number for that video in your `manifest.json`.

```json
{
  "videos": {
    "intro": { "version": 2, "url": "https://your-host.com/videos/intro_v2.mp4" }
  }
}
```

On next launch, any device with internet will download the new file silently in the background. Devices without internet continue playing the bundled version until connectivity is restored.

---

## Enabling Remote Updates

By default, remote updates are disabled. To enable them:

1. Host a `manifest.json` file somewhere publicly accessible (GitHub Releases, a CDN, a simple web server).

2. Set `MANIFEST_URL` in `logic/VideoConfig.kt`:

   ```kotlin
   object VideoConfig {
       const val MANIFEST_URL = "https://your-host.com/manifest.json"
   }
   ```

### Manifest format

```json
{
  "videos": {
    "intro":            { "version": 1, "url": "https://your-host.com/videos/intro_v1.mp4" },
    "outro":            { "version": 1, "url": "https://your-host.com/videos/outro_v1.mp4" },
    "ai_speech_bubble": { "version": 1, "url": "https://your-host.com/videos/ai_speech_bubble_v1.mp4" }
  }
}
```

- **`version`** — integer, increment to trigger a download on all devices.
- **`url`** — direct link to the video file. Must be publicly accessible (no authentication).
- The key (`"intro"`, `"outro"`, etc.) must match `VideoAsset.key`, which is the file name without the `.mp4` extension.

---

## Compressing Videos for the Bundled APK

The bundled videos are included in the APK and should be as small as possible. Use FFmpeg with H.265 encoding:

```bash
# Good quality, significantly smaller (recommended starting point)
ffmpeg -i input.mp4 -vcodec libx265 -crf 24 -preset slow -acodec aac output.mp4

# Smaller file, slightly lower quality
ffmpeg -i input.mp4 -vcodec libx265 -crf 28 -preset slow -acodec aac output.mp4
```

**CRF guide:**
- `18` — visually lossless
- `24` — excellent quality, large file size reduction (recommended)
- `28` — good quality, maximum compression

Test the result on the actual tablet before replacing the bundled asset. A 200 MB source file typically compresses to 15–30 MB at CRF 24 with no visible difference on a tablet screen.

---

## How Updates Work Internally

`VideoUpdateManager.checkAndUpdate()` is called once in `GameViewModel.init {}` and runs on a background coroutine via `viewModelScope`.

1. If `VideoConfig.MANIFEST_URL` is empty, the method returns immediately (no network activity).
2. If there is no active internet connection, the method returns immediately.
3. The manifest JSON is fetched and parsed.
4. For each `VideoAsset`, the remote `version` is compared against a locally stored version in `SharedPreferences` (`"video_versions"`).
5. If the remote version is higher, the file is downloaded to `filesDir/videos/` using a temp-file-then-rename strategy to ensure a failed download never leaves a corrupted file.
6. On success, the new version number is stored in `SharedPreferences`.

All failures are swallowed silently — the bundled asset always serves as the fallback.

---

## Offline Behaviour

The app works fully offline at all times. `VideoAssetManager.getUri()` checks for a downloaded file in `filesDir` first; if none exists it falls back to the bundled `asset:///videos/` URI. No internet connection is ever required to play videos.
