# Video Assets

Place the following MP4 files in this folder:

| File | Description |
|------|-------------|
| `intro.mp4` | Intro video shown at the start of the game (before difficulty selection) |
| `outro.mp4` | Outro video shown on the Congratulations screen after all missions are completed |
| `ai_speech_bubble.mp4` | Animated AI speech bubble that plays alongside the AI voice narration |

## Notes
- Files should be in MP4 format (H.264 video, AAC audio recommended for best Android compatibility)
- Keep file sizes reasonable for tablet storage — compress if needed
- These files are loaded from `assets/videos/` at runtime via `AssetFileDescriptor`
