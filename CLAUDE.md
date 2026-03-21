# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CodeNPlay is an Android escape room educational game for kids and teens learning coding concepts. Players progress through 4 missions (Binary decoding, Scratch, AI, Robot) with difficulty split between Junior Agent (kids) and Senior Agent (teens).

## Build & Run Commands

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Install debug build on connected device/emulator
./gradlew test                   # Run unit tests
./gradlew connectedAndroidTest   # Run instrumented tests (requires device/emulator)
./gradlew lint                   # Run Android lint checks
```

**Requirements:** JDK 11+, Android SDK API 36, Android Studio.

## Architecture

Single-activity Jetpack Compose app (Kotlin 2.0.21, Material 3, min SDK 24, target SDK 36, forced landscape).

**State** lives in `GameViewModel` — tracks selected difficulty (`KIDS`/`TEENS`) and game start time. All screens receive the ViewModel via `navController` and read `missionConfig` from it.

**Mission flow** (defined in `NavGraph.kt`):
```
HOME → VIDEO → DIFFICULTY → BINARY_GAME
  → BINARY_QUIZ {word} → SCRATCH_GAME → SCRATCH_QUIZ
  → AI_GAME → AI_QUIZ → ROBOT_GAME → ROBOT_QUIZ
  → CONGRATULATIONS
```

**Difficulty configuration** is entirely driven by `MissionConfig.kt`. Two singleton instances (`JUNIOR_AGENT`, `SENIOR_AGENT`) hold access codes, timer durations, robot instructions, and `QuizConfig` objects for every mission. To change game content, edit `MissionConfig.kt` and the string resources — not the screen composables.

**Binary puzzle** (`BinaryGameScreen.kt` + `BinaryPuzzle.kt`): randomly selects one of 5 words (DATA/CODE/SERVER/ROBOT/CLOUD), converts to 8-bit binary, user types the decoded word. Correct answer routes to the word-specific quiz pulled from `WordQuizData.kt`.

**Quiz system** (`QuizScreen.kt`): generic composable that renders any `QuizConfig` — question + 3 shuffled options (all `@StringRes` references) + explanation after submission.

**Localization**: English (`values/strings.xml`), Dutch (`values-nl/`), French (`values-fr/`). Language switching via `AppCompatDelegate.setApplicationLocales()` on the home screen.

## Key Files for Common Changes

| Change | File |
|--------|------|
| Access codes / timer / robot instructions | `logic/MissionConfig.kt` |
| Binary word quiz questions | `logic/WordQuizData.kt` |
| All UI text and quiz content | `res/values/strings.xml` (+ `nl`, `fr` variants) |
| App color palette | `theme/Color.kt` |
| Font sizes | `theme/Type.kt` |
| Background grid pattern | `theme/MissionControlBackground.kt` |

## Dependency Management

Dependencies are declared in `gradle/libs.versions.toml` (version catalog). Add new deps there, then reference them in `app/build.gradle.kts` via `libs.*` aliases.