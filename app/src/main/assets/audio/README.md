# Audio Assets

Drop MP3 files here. Each screen has one audio file per language.

## Naming convention
`[screen]_[language].mp3` or `[screen]_[level]_[language].mp3`

Levels: `junior`, `senior`
Languages: `en`, `nl`, `fr`

## Files

| File | Plays on | Status |
|------|----------|--------|
| `binary_en.mp3` / `binary_nl.mp3` / `binary_fr.mp3` | Binary puzzle screen | ✅ All present |
| `scratch_en.mp3` / `scratch_nl.mp3` / `scratch_fr.mp3` | Scratch mission screen | ✅ All present |
| `ai_junior_en.mp3` / `ai_junior_nl.mp3` / `ai_junior_fr.mp3` | AI mission screen (Junior Agent) | ✅ All present |
| `ai_senior_en.mp3` / `ai_senior_nl.mp3` / `ai_senior_fr.mp3` | AI mission screen (Senior Agent) | ✅ All present |
| `AI_ENG.mpeg` / `AI_NL.mpeg` / `AI_FR.mpeg` | AI Senior — BAD_PREDICTION phase | ✅ All present |
| `robot_en.mp3` / `robot_nl.mp3` / `robot_fr.mp3` | Robot mission screen | ✅ All present |

## Fallback

`test.mp3` is used as fallback when a locale-specific file is missing.

## Notes

- The Outro screen uses subtitles only (no audio file needed).
- `AiGameIntroScreen` is not currently connected to the navigation graph and its `intro_*.mp3` files are not in use.
