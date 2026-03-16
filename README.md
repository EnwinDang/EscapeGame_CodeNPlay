# CodeNPlay - Escape Room Mission App

A tablet-based escape room app for kids and teens. Players take on the role of computer agents and complete 4 progressive coding missions to restore a compromised datacenter system.

## Overview

Players choose a difficulty level and work through missions involving binary decoding, Scratch programming, AI concepts, and robot programming. Each mission ends with a quiz to reinforce the topic.

**Difficulty levels:**
- **Junior Agent** (Kids) — longer timer, Ozobot robot station, age-appropriate questions
- **Senior Agent** (Teens) — shorter timer, LEGO Spike robot station, more advanced questions

**Languages supported:** English, Dutch, French (switchable in-app)

## Game Flow

```
Home Screen
  → Difficulty Selection (Junior / Senior Agent)
  → Mission 1: Binary Decoder (timed puzzle)
  → Binary Word Quiz
  → Mission 2: Scratch Protocol (enter code "LOOP")
  → Scratch Quiz
  → Mission 3: AI Protocol (enter code "AI")
  → AI Quiz
  → Mission 4: Robot Protocol (enter code "ROBOT")
  → Robot Quiz
  → Congratulations Screen
```

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Navigation:** AndroidX Navigation Compose
- **State:** ViewModel + Compose State
- **Design:** Material Design 3
- **Min SDK:** 24 | **Target SDK:** 36

## Project Structure

```
app/src/main/java/com/example/escapegame/
├── logic/           # Game mechanics & configuration
│   ├── MissionConfig.kt   # All difficulty-specific settings
│   ├── BinaryPuzzle.kt    # Binary encoding/decoding
│   ├── WordQuizData.kt    # Quiz questions per word
│   └── QuizConfig.kt      # Quiz data class
├── screens/         # Compose UI screens
├── viewmodel/       # GameViewModel (game state)
├── navigation/      # NavGraph (full mission flow)
├── theme/           # Colors, typography, background
└── MainActivity.kt

app/src/main/res/
├── values/          # English strings
├── values-nl/       # Dutch strings
└── values-fr/       # French strings
```

## Building & Running

**Requirements:** Android Studio, JDK 11+

```bash
# Build
./gradlew build

# Install on connected device/emulator
./gradlew installDebug
```

The app runs in **forced landscape orientation**, designed for tablets.

## Configuration

All difficulty-specific settings live in `MissionConfig.kt`:

- Access codes per mission (e.g. `scratchCode = "LOOP"`)
- Timer duration (`binaryTimerSeconds`)
- Robot station instructions (`robotInstructionsRes`)
- Quiz questions and explanations

All text content (including quiz questions) is in the `res/values/strings.xml` files, with separate sections for Junior and Senior agents.

## Developer Guide

See [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) for detailed documentation on adding missions, modifying quizzes, and extending the app.
