# CodeNPlay — Developer Guide

> 🇬🇧 English documentation below — 🇫🇷 La documentation française suit en dessous

> 📲 **Looking for installation instructions (how to put the app on a tablet)?**
> See **[README.md](README.md)** — it contains step-by-step instructions in both English and French for all 3 installation methods (Android Studio, APK file, ADB).

---

# 🇬🇧 ENGLISH

## Table of Contents

1. [App Architecture](#1-app-architecture)
2. [Mission Flow & Navigation](#2-mission-flow--navigation)
3. [Difficulty System](#3-difficulty-system)
4. [Access Codes — Quick Reference](#4-access-codes--quick-reference)
5. [Quiz System](#5-quiz-system)
6. [Binary Puzzle](#6-binary-puzzle)
7. [Audio System (Speech Bubbles)](#7-audio-system-speech-bubbles)
8. [Video System](#8-video-system)
9. [Language / Localisation System](#9-language--localisation-system)
10. [Screen-by-Screen Reference](#10-screen-by-screen-reference)
11. [How to Change Things](#11-how-to-change-things)
12. [How to Add a New Mission](#12-how-to-add-a-new-mission)
13. [File Map](#13-file-map)
14. [Quiz Content Reference](#14-quiz-content-reference)

---

## 1. App Architecture

CodeNPlay is a **single-Activity Jetpack Compose** app written in Kotlin.

```
MainActivity
  └── EscapeGameTheme
        └── NavHost (NavGraph.kt)
              └── [one Composable per screen]
```

- **State** lives in `GameViewModel`. It holds the selected difficulty and the start time. All screens read `viewModel.missionConfig` to get difficulty-specific data.
- **Navigation** is handled by AndroidX Navigation Compose. The route is linear — players cannot go backwards during a game session.
- **Orientation** is forced to landscape in `AndroidManifest.xml`. The app is designed only for tablets in landscape.
- **Theme** is a single fixed dark scheme (`EscapeGameTheme` in `theme/Theme.kt`). There is no light theme.

---

## 2. Mission Flow & Navigation

The full navigation graph is defined in `navigation/NavGraph.kt`.

```
HOME
  → VIDEO
  → DIFFICULTY
  → BINARY_GAME
  → BINARY_QUIZ/{word}       ← word = decoded binary word (DATA/CODE/SERVER/ROBOT/CLOUD)
  → SCRATCH_GAME
  → SCRATCH_QUIZ
  → AI_GAME                  ← Junior: AiGameScreen / Senior: AiValidationScreen
  → AI_QUIZ
  → ROBOT_GAME
  → ROBOT_QUIZ
  → OUTRO
  → CONGRATULATIONS
```

**Key rules:**
- The back button is **disabled** on all game screens (`BackHandler(enabled = true) {}`). Players cannot go back mid-mission.
- The **Home button** (top-left or top-right on each screen) shows a confirmation dialog and resets the game via `viewModel.reset()`.
- The `BINARY_QUIZ` route uses a **route argument** (`binary_quiz/{word}`) so the correct quiz loads for the decoded word.
- The `AI_GAME` route checks `viewModel.difficulty` at runtime and renders either `AiGameScreen` (Junior) or `AiValidationScreen` (Senior).

---

## 3. Difficulty System

Two pre-built difficulty configs exist in `logic/MissionConfig.kt`:

| Kotlin object | Label | Target |
|---|---|---|
| `JUNIOR_AGENT` | Junior Agent | Kids |
| `SENIOR_AGENT` | Senior Agent | Teens |

When a player selects a difficulty on the Difficulty screen, `GameViewModel.startGame(difficulty)` is called. This sets `viewModel.missionConfig` to either `JUNIOR_AGENT` or `SENIOR_AGENT`.

Every screen that needs difficulty-specific data receives it via the `missionConfig` object (or `uiStyle` for visual differences).

**The `MissionConfig` data class holds:**

```kotlin
data class MissionConfig(
    val binaryTimerSeconds: Int,          // timer for Mission 1
    val scratchCode: String,              // code players type at Scratch station
    val aiCode: String,                   // code players type at AI station
    val robotCode: String,                // code players type at Robot station
    val robotInstructionsRes: Int,        // @StringRes — which robot instructions to show
    val wordQuizMap: Map<String, QuizConfig>,  // binary word → quiz (for Mission 1)
    val scratchQuiz: QuizConfig,
    val aiQuiz: QuizConfig,
    val robotQuiz: QuizConfig,
    val uiStyle: AgentUiStyle,            // JUNIOR or SENIOR (for visual differences)
)
```

---

## 4. Access Codes — Quick Reference

These are the codes players must type to unlock each mission. Defined in `MissionConfig.kt`.

| Mission | Code (Junior & Senior) | Notes |
|---|---|---|
| Mission 1 — Binary | *(no code — solved by decoding)* | Word is random: DATA / CODE / SERVER / ROBOT / CLOUD |
| Mission 2 — Scratch | `LOOP` | Player goes to the laptop and gets this code |
| Mission 3 — AI | `AI` | Junior: from mini-game / Senior: from micro:bit |
| Mission 4 — Robot | `ROBOT` | Junior: from Ozobot / Senior: from LEGO Spike |

> To change a code, edit `scratchCode`, `aiCode`, or `robotCode` in `MissionConfig.kt`. The code check is case-insensitive (`.trim().uppercase()`), so `loop`, `Loop`, and `LOOP` all work.

---

## 5. Quiz System

Every mission ends with a quiz. All quizzes use the same generic composable: `screens/QuizScreen.kt`.

**How a quiz is structured — `QuizConfig`:**

```kotlin
data class QuizConfig(
    val questionRes: Int,    // @StringRes — the question text
    val optionARes: Int,     // @StringRes — first answer option
    val optionBRes: Int,     // @StringRes — second answer option
    val optionCRes: Int,     // @StringRes — third answer option
    val correctIndex: Int,   // 0 = A, 1 = B, 2 = C
    val explanationRes: Int, // @StringRes — shown after the player answers
)
```

**How `QuizScreen` works:**
1. The 3 options are displayed in shuffled order (randomised each time the screen loads).
2. When a player picks an answer, they see ✓ or ✗ immediately.
3. The explanation is always shown after submission, whether the player was right or wrong.
4. A "Proceed" button then appears to continue to the next mission.

**Binary quiz — special case:**
Mission 1 ends by calling `onSolved(puzzle.currentWord)`, which navigates to `binary_quiz/{word}`. The NavGraph then looks up `config.wordQuizMap[word]` to find the right quiz for that word. If the word is not found, it falls back to the DATA quiz.

---

## 6. Binary Puzzle

The binary puzzle is generated in `logic/BinaryPuzzle.kt`.

**How it works:**
1. A word is randomly chosen from the pool: `DATA`, `CODE`, `SERVER`, `ROBOT`, `CLOUD`.
2. Each letter is converted to its 8-bit binary ASCII representation.
3. The binary groups are displayed on screen, separated by spaces.
4. The player types the decoded word into the text field.
5. `checkAnswer()` compares the input (trimmed + uppercased) against the target word.

**To add a new binary word:**
1. Add the word to the list in `BinaryPuzzle.kt`
2. Add a `QuizConfig` entry for it in `logic/WordQuizData.kt` (for both `juniorWordQuizMap` and `seniorWordQuizMap`)
3. Add all string resources for the new quiz (`word_XXX_question_kids`, `word_XXX_option_a_kids`, etc.) to all three `strings.xml` files (EN, FR, NL)

---

## 7. Audio System (Speech Bubbles)

Each game screen has an **AI speech bubble** — a circular video avatar that plays alongside a voiceover MP3.

**How it works:**
- The video (looping `ai_speech_bubble.mp4`) is rendered by `AISpeechBubble.kt` using ExoPlayer.
- The audio (locale-specific `.mp3`) is played by a `MediaPlayer` directly in the screen composable.
- When the audio finishes, `isBubblePlaying` is set to `false` — the bubble shows a play icon.
- Tapping the bubble toggles pause/play for both the audio and the bubble visual state.
- On screens where audio must finish before proceeding (Binary, AI Senior intro), the button is `enabled = hasFinishedAudio`.

**Audio file naming convention:**
```
audio/[screen]_[locale].mp3
audio/[screen]_[level]_[locale].mp3
```

| File name | Used on screen |
|---|---|
| `binary_fr.mp3` | Binary puzzle (FR) |
| `scratch_fr.mp3` | Scratch mission (FR) |
| `ai_junior_fr.mp3` | AI Junior game intro (FR) |
| `ai_senior_fr.mp3` | AI Senior validation intro (FR) |
| `robot_fr.mp3` | Robot mission (FR) |

**Fallback chain:** If `binary_fr.mp3` is missing, the app tries `test.mp3`. If that too is missing, audio is silently skipped. `AiGameScreen` has an extra fallback step: it tries `ai_junior_[locale].mp3` → `ai_[locale].mp3` → `test.mp3`.

**Screens where audio completion gates the next button:**
Only two screens disable their action button until `hasFinishedAudio = true`:
- `AiGameScreen` (Junior) — "Start Mission" button
- `AiValidationScreen` (Senior) — "Start Validation" button

All other screens (Binary, Scratch, Robot) play audio automatically but **never block interaction**.

> ⚠️ **Dead file:** `AiGameIntroScreen.kt` exists in the codebase with its own `hasFinishedAudio` logic, but it is **not referenced anywhere in `NavGraph.kt`**. It is unused and can be safely deleted or ignored.

**To add English or Dutch audio:**
Simply add `binary_en.mp3`, `binary_nl.mp3`, `scratch_en.mp3`, etc. to `app/src/main/assets/audio/`. The app will automatically pick up the right file based on the active language.

---

## 8. Video System

Videos are managed by `logic/VideoAssetManager.kt` and `logic/VideoAsset.kt`.

**Video files go in:** `app/src/main/assets/videos/`

| File | Screen | Notes |
|---|---|---|
| `intro.mp4` | `VideoScreen.kt` | Plays before difficulty selection. If missing, screen shows a placeholder. |
| `outro.mp4` | `OutroScreen.kt` | Plays after all missions are complete. |
| `ai_speech_bubble.mp4` | `AISpeechBubble.kt` | Looping animated avatar. Plays on Binary, Scratch, AI, and Robot screens. |

**Technical details:**
- Videos are loaded via `AssetFileDescriptor` — they are bundled inside the APK.
- The speech bubble uses **ExoPlayer** with `REPEAT_MODE_ONE` (loops forever).
- The intro/outro videos use ExoPlayer and auto-advance when the video ends.
- Recommended format: **MP4, H.264 video, AAC audio**, resolution suitable for tablets (720p or 1080p).
- Keep file sizes reasonable — large videos increase the APK size significantly.

---

## 9. Language / Localisation System

The app uses Android's built-in string resource system with `AppCompatDelegate.setApplicationLocales()` for runtime language switching.

**Supported languages:**

| Code | Language | File location |
|---|---|---|
| `en` | English | `res/values/strings.xml` |
| `nl` | Dutch | `res/values-nl/strings.xml` |
| `fr` | French | `res/values-fr/strings.xml` |

**How language switching works:**
1. Player taps the 🌐 globe on the Home screen.
2. `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fr"))` is called.
3. Android recreates the Activity with the new locale — all `stringResource()` calls now return French strings.
4. The selection persists across app restarts.

**How audio language selection works:**
```kotlin
val locale = AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
val audioPath = "audio/binary_$locale.mp3"
```
This runs at screen load time and picks the correct file for the current language.

**To add a new language (e.g. German):**
1. Create `res/values-de/strings.xml`
2. Copy all string keys from `res/values/strings.xml`
3. Translate all values
4. Add `"de"` to the `languages` list in `HomeScreen.kt`
5. Add `"de" to "DE"` to `languageLabels` in `HomeScreen.kt`
6. Add audio files: `binary_de.mp3`, `scratch_de.mp3`, etc.

---

## 10. Screen-by-Screen Reference

### `HomeScreen.kt`
- Entry point. Player taps anywhere to continue.
- Contains the language picker (top-right globe button).
- "MISSION CONTROL" title with floating animation.
- The `home_tap_to_start` string is the pulsing white text at the bottom.

### `VideoScreen.kt`
- Plays `intro.mp4` full-screen.
- Auto-advances to the Difficulty screen when the video ends.
- If the video file is missing, shows a placeholder with a "Continue" button.

### `DifficultyScreen.kt`
- Two agent cards (Junior / Senior), each with bullet-point feature lists.
- Selecting a card reveals the "CONFIRM — AGENT JUNIOR/SENIOR" button.
- All text comes from string resources (`junior_bullet_1` through `junior_bullet_5`, etc.).
- The confirm button label uses `difficulty_confirm` + `btn_kids`/`btn_teens` string resources (fully translated).

### `BinaryGameScreen.kt`
- Shows the encoded binary string from `BinaryPuzzle` immediately — there is no start button.
- Top bar: home button, 4-mission progress indicator, countdown timer.
- Speech bubble plays `binary_[locale].mp3`. Audio plays automatically; when it ends the bubble shows a play icon. **Nothing is gated on audio completion** — the puzzle is always interactive.
- Submitting a correct answer navigates to `binary_quiz/{word}`.
- Timer expiry shows a "Override — proceed anyway" option.

### `QuizScreen.kt`
- Generic quiz composable used after every mission.
- Receives `question`, `options` (List of 3), `correctIndex`, `explanation`, `uiStyle`.
- Options are shuffled randomly at load time.
- After submission, shows explanation and a "Proceed" button.

### `ExternalGameScreen.kt`
- Used for Scratch (step 2) and Robot (step 4) missions.
- Shows: mission label, title, Scratch logo (only on step 2), speech bubble + instructions, lock button.
- Player taps the lock to reveal the code input field.
- Submitting the correct code navigates forward. Wrong code shakes the screen.
- Speech bubble plays `scratch_[locale].mp3` or `robot_[locale].mp3`.

### `AiGameScreen.kt` (Junior only)
- Three phases: `INTRO → COUNTDOWN → GAME`.
- **INTRO:** Speech bubble plays `ai_junior_[locale].mp3`. Shows 4 instruction lines. Start button disabled until audio finishes.
- **COUNTDOWN:** Displays 3... 2... 1... GO! full-screen.
- **GAME:** 5 rounds of icon-spotting. One icon per grid is different — player must tap it. Speed increases each round. After 5 correct taps, game is won.

### `AiValidationScreen.kt` (Senior only)
- Multi-phase micro:bit validation flow: `INTRO → FIRST_RUN → CODE_INPUT_1 → BAD_PREDICTION → RECALIBRATE → UPLOADING → SECOND_RUN → CODE_INPUT_2 → SUCCESS`.
- **INTRO:** Speech bubble plays `ai_senior_[locale].mp3`. Start button disabled until audio finishes.
- **BAD_PREDICTION:** 2-step reveal. First shows the error panel with a "Continue" button. After Continue, shows the diagnosis + "Recalibrate" button.
- **UPLOADING:** Animated progress bar (3 seconds), then auto-advances.
- **CODE_INPUT_2:** Only the correct code (`config.aiCode`) advances to SUCCESS.
- A status indicator (top-left) shows the current phase in colour throughout.

### `CongratulationsScreen.kt`
- Shows mission complete, total time, and difficulty level.
- "New Mission" button resets the game and returns to Home.

---

## 11. How to Change Things

### Change an access code

**File:** `logic/MissionConfig.kt`

```kotlin
val JUNIOR_AGENT = MissionConfig(
    scratchCode = "LOOP",   // ← change to any uppercase string
    aiCode      = "AI",
    robotCode   = "ROBOT",
    ...
)
```

The check in each screen is `.trim().uppercase()`, so the player's input is normalised — any casing works.

---

### Change the timer duration

**File:** `logic/MissionConfig.kt`

```kotlin
binaryTimerSeconds = 10 * 60   // 10 minutes → change to e.g. 8 * 60 for 8 minutes
```

---

### Change quiz question text

**Files:** `res/values/strings.xml`, `res/values-fr/strings.xml`, `res/values-nl/strings.xml`

Find the relevant string key and edit the text. Pattern:
- `word_data_question_kids` → junior quiz question for binary word DATA
- `scratch_quiz_option_a_teens` → first option in the senior scratch quiz
- Always edit **all three language files** when changing quiz content.

---

### Change which option is correct

**File:** `logic/MissionConfig.kt` (for Scratch/AI/Robot) or `logic/WordQuizData.kt` (for Binary)

Change `correctIndex`:
- `0` = option A is correct
- `1` = option B is correct
- `2` = option C is correct

> ⚠️ **Important:** Currently **every single quiz** in both `WordQuizData.kt` and `MissionConfig.kt` has `correctIndex = 0`, meaning **option A is always the correct answer**. If you add a new quiz where option B or C is correct, you **must** change `correctIndex` to `1` or `2` — otherwise option A will be marked correct regardless of the text.

> Note: options are **shuffled** at runtime in `QuizScreen.kt`. `correctIndex` refers to the order defined in the strings file (A/B/C), not the visual position shown to the player.

---

### Add a new binary word

1. **`logic/BinaryPuzzle.kt`** — add the word to the random selection pool
2. **`logic/WordQuizData.kt`** — add a `QuizConfig` for the word in both `juniorWordQuizMap` and `seniorWordQuizMap`
3. **All three `strings.xml` files** — add 5 new strings per difficulty:
   - `word_XXX_question_kids` / `_teens`
   - `word_XXX_option_a_kids` / `_teens`
   - `word_XXX_option_b_kids` / `_teens`
   - `word_XXX_option_c_kids` / `_teens`
   - `word_XXX_explanation_kids` / `_teens`

---

### Change robot instructions

**Files:**
- `logic/MissionConfig.kt` → `robotInstructionsRes` (which string resource to use)
- All three `strings.xml` → `robot_instructions_kids` / `robot_instructions_teens`

---

### Add or update audio

Drop the MP3 file into `app/src/main/assets/audio/` with the correct name:
```
[screen]_[locale].mp3
```
For example: `binary_en.mp3`, `scratch_nl.mp3`.

The app picks the file automatically based on the active language. No code changes needed.

---

### Change the home screen "tap to start" text size

**File:** `screens/HomeScreen.kt`

Find:
```kotlin
fontSize = 18.sp // ← "tap anywhere" text
```

---

### Change colours

**File:** `theme/Color.kt`

Key colours:
- `MatrixGreen` — the main green colour (borders, active elements, progress bars)
- `BrandYellow` — yellow accent (titles, Junior Agent colour)
- `BrandBlue` — blue accent (labels, Senior Agent colour)
- `ErrorRed` — used for errors and the BAD_PREDICTION screen

---

## 12. How to Add a New Mission

Adding a mission requires changes in 4 places:

**1. Add a route in `navigation/NavGraph.kt`:**
```kotlin
object Routes {
    const val NEW_MISSION = "new_mission"
}
```
Then add a `composable(Routes.NEW_MISSION) { ... }` block.

**2. Wire up the navigation:**
Change the previous screen's `onContinue` to navigate to `Routes.NEW_MISSION` instead of wherever it went before.

**3. Add a new screen composable in `screens/`.**

**4. Add a quiz config in `MissionConfig.kt`:**
```kotlin
val newMissionQuiz: QuizConfig
```
And add the string resources in all three `strings.xml` files.

---

## 13. File Map

```
logic/
  MissionConfig.kt        ← START HERE for all difficulty-specific config
  QuizConfig.kt           ← Data class: question + 3 options + explanation
  WordQuizData.kt         ← Binary word quiz maps (junior + senior)
  BinaryPuzzle.kt         ← Binary puzzle generation and answer checking
  VideoAsset.kt           ← Enum of video asset names
  VideoAssetManager.kt    ← Loads video files from assets/
  VideoConfig.kt          ← Video configuration
  VideoUpdateManager.kt   ← Handles video state

viewmodel/
  GameViewModel.kt        ← Stores difficulty, start time, exposes missionConfig

navigation/
  NavGraph.kt             ← Full mission flow / all routes

screens/
  HomeScreen.kt           ← Landing screen with language picker
  VideoScreen.kt          ← Intro video (intro.mp4)
  DifficultyScreen.kt     ← Junior / Senior Agent selection
  BinaryGameScreen.kt     ← Mission 1: Binary Decoder
  QuizScreen.kt           ← Generic quiz (used after every mission)
  ExternalGameScreen.kt   ← Scratch (M2) + Robot (M4) code-entry screens
  AiGameScreen.kt         ← Mission 3 Junior: icon-spotting game
  AiValidationScreen.kt   ← Mission 3 Senior: micro:bit AI validation
  AISpeechBubble.kt       ← Circular video + audio composable
  CongratulationsScreen.kt← Mission Complete screen
  OutroScreen.kt          ← Outro video screen
  HomeButton.kt           ← Reusable home/back button with confirmation dialog

theme/
  MissionControlBackground.kt ← Dark background with animated cyan grid
  Color.kt                ← Full colour palette
  Type.kt                 ← Typography scale (enlarged for tablets)
  Theme.kt                ← EscapeGameTheme (dark theme only)

res/values/strings.xml    ← English text + all quiz content
res/values-fr/strings.xml ← French translations
res/values-nl/strings.xml ← Dutch translations

assets/audio/             ← MP3 voiceover files ([screen]_[locale].mp3)
assets/videos/            ← MP4 video files (intro, outro, ai_speech_bubble)
```

---

## 14. Quiz Content Reference

### Pattern
> Every quiz relates to the code or word the player just interacted with.
> Decode `SERVER` → quiz about servers. Type `LOOP` → quiz about loops. Type `AI` → quiz about AI.

### Binary Word Quizzes

| Word | Junior question | Senior question |
|---|---|---|
| DATA | What is data? | Data vs information — what's the difference? |
| CODE | What is code in programming? | What is the purpose of a function? |
| SERVER | What is a server? | In a client-server model, what does the client do? |
| ROBOT | How does a robot know what to do? | What does 'autonomous' mean for a robot? |
| CLOUD | What is "the cloud"? | Key advantage of cloud computing for a datacenter? |

### Mission Quizzes

| Mission | Junior question | Senior question |
|---|---|---|
| Scratch | What is Scratch used for? | What does a 'repeat' block represent? |
| AI | What does AI stand for? | How does a machine learning model improve? |
| Robot | How does a robot know what to do? | Sensor vs actuator — what's the difference? |

---
---

# 🇫🇷 FRANÇAIS

## Table des matières

1. [Architecture de l'application](#1-architecture-de-lapplication)
2. [Flux de missions et navigation](#2-flux-de-missions-et-navigation)
3. [Système de difficulté](#3-système-de-difficulté)
4. [Codes d'accès — Référence rapide](#4-codes-daccès--référence-rapide)
5. [Système de quiz](#5-système-de-quiz)
6. [Puzzle binaire](#6-puzzle-binaire)
7. [Système audio (bulles de parole)](#7-système-audio-bulles-de-parole)
8. [Système vidéo](#8-système-vidéo)
9. [Système de langues / localisation](#9-système-de-langues--localisation)
10. [Référence écran par écran](#10-référence-écran-par-écran)
11. [Comment modifier les choses](#11-comment-modifier-les-choses)
12. [Comment ajouter une nouvelle mission](#12-comment-ajouter-une-nouvelle-mission)
13. [Carte des fichiers](#13-carte-des-fichiers)
14. [Référence du contenu des quiz](#14-référence-du-contenu-des-quiz)

---

## 1. Architecture de l'application

CodeNPlay est une application **Jetpack Compose à activité unique** écrite en Kotlin.

```
MainActivity
  └── EscapeGameTheme
        └── NavHost (NavGraph.kt)
              └── [un Composable par écran]
```

- **L'état** vit dans `GameViewModel`. Il contient la difficulté sélectionnée et l'heure de début. Tous les écrans lisent `viewModel.missionConfig` pour obtenir les données spécifiques à la difficulté.
- **La navigation** est gérée par AndroidX Navigation Compose. Le parcours est linéaire — les joueurs ne peuvent pas reculer pendant une session de jeu.
- **L'orientation** est forcée en paysage dans `AndroidManifest.xml`. L'application est conçue uniquement pour les tablettes en paysage.
- **Le thème** est un schéma sombre unique (`EscapeGameTheme` dans `theme/Theme.kt`). Il n'y a pas de thème clair.

---

## 2. Flux de missions et navigation

Le graphe de navigation complet est défini dans `navigation/NavGraph.kt`.

```
HOME
  → VIDEO
  → DIFFICULTY
  → BINARY_GAME
  → BINARY_QUIZ/{word}       ← word = mot binaire décodé (DATA/CODE/SERVER/ROBOT/CLOUD)
  → SCRATCH_GAME
  → SCRATCH_QUIZ
  → AI_GAME                  ← Junior : AiGameScreen / Senior : AiValidationScreen
  → AI_QUIZ
  → ROBOT_GAME
  → ROBOT_QUIZ
  → OUTRO
  → CONGRATULATIONS
```

**Règles importantes :**
- Le bouton retour est **désactivé** sur tous les écrans de jeu (`BackHandler(enabled = true) {}`). Les joueurs ne peuvent pas reculer en cours de mission.
- Le **bouton Home** (en haut à gauche ou à droite) affiche une boîte de dialogue de confirmation et réinitialise le jeu via `viewModel.reset()`.
- La route `BINARY_QUIZ` utilise un **argument de route** (`binary_quiz/{word}`) pour charger le bon quiz selon le mot décodé.
- La route `AI_GAME` vérifie `viewModel.difficulty` à l'exécution et affiche soit `AiGameScreen` (Junior) soit `AiValidationScreen` (Senior).

---

## 3. Système de difficulté

Deux configurations de difficulté prédéfinies existent dans `logic/MissionConfig.kt` :

| Objet Kotlin | Label | Public cible |
|---|---|---|
| `JUNIOR_AGENT` | Agent Junior | Enfants |
| `SENIOR_AGENT` | Agent Senior | Ados |

Quand un joueur sélectionne une difficulté, `GameViewModel.startGame(difficulty)` est appelé. Cela définit `viewModel.missionConfig` sur `JUNIOR_AGENT` ou `SENIOR_AGENT`.

**La classe `MissionConfig` contient :**

```kotlin
data class MissionConfig(
    val binaryTimerSeconds: Int,          // minuterie pour la Mission 1
    val scratchCode: String,              // code que les joueurs tapent à la station Scratch
    val aiCode: String,                   // code que les joueurs tapent à la station IA
    val robotCode: String,                // code que les joueurs tapent à la station Robot
    val robotInstructionsRes: Int,        // @StringRes — quelles instructions robot afficher
    val wordQuizMap: Map<String, QuizConfig>,  // mot binaire → quiz (pour Mission 1)
    val scratchQuiz: QuizConfig,
    val aiQuiz: QuizConfig,
    val robotQuiz: QuizConfig,
    val uiStyle: AgentUiStyle,            // JUNIOR ou SENIOR (pour les différences visuelles)
)
```

---

## 4. Codes d'accès — Référence rapide

Ce sont les codes que les joueurs doivent taper pour débloquer chaque mission. Définis dans `MissionConfig.kt`.

| Mission | Code (Junior et Senior) | Notes |
|---|---|---|
| Mission 1 — Binaire | *(pas de code — résolu par décodage)* | Mot aléatoire : DATA / CODE / SERVER / ROBOT / CLOUD |
| Mission 2 — Scratch | `LOOP` | Le joueur va au laptop et obtient ce code |
| Mission 3 — IA | `AI` | Junior : depuis le mini-jeu / Senior : depuis le micro:bit |
| Mission 4 — Robot | `ROBOT` | Junior : depuis l'Ozobot / Senior : depuis LEGO Spike |

> La vérification est insensible à la casse (`.trim().uppercase()`), donc `loop`, `Loop` et `LOOP` fonctionnent tous.

---

## 5. Système de quiz

Chaque mission se termine par un quiz. Tous les quiz utilisent le même composable générique : `screens/QuizScreen.kt`.

**Structure d'un quiz — `QuizConfig` :**

```kotlin
data class QuizConfig(
    val questionRes: Int,    // @StringRes — le texte de la question
    val optionARes: Int,     // @StringRes — première option de réponse
    val optionBRes: Int,     // @StringRes — deuxième option de réponse
    val optionCRes: Int,     // @StringRes — troisième option de réponse
    val correctIndex: Int,   // 0 = A, 1 = B, 2 = C
    val explanationRes: Int, // @StringRes — affiché après que le joueur répond
)
```

**Comment `QuizScreen` fonctionne :**
1. Les 3 options sont affichées dans un ordre mélangé (aléatoire à chaque chargement).
2. Quand le joueur choisit une réponse, il voit ✓ ou ✗ immédiatement.
3. L'explication est toujours affichée après la soumission, que le joueur ait eu raison ou tort.
4. Un bouton "Procéder" apparaît ensuite pour continuer à la mission suivante.

---

## 6. Puzzle binaire

Le puzzle binaire est généré dans `logic/BinaryPuzzle.kt`.

**Comment ça marche :**
1. Un mot est choisi aléatoirement parmi : `DATA`, `CODE`, `SERVER`, `ROBOT`, `CLOUD`.
2. Chaque lettre est convertie en sa représentation ASCII binaire sur 8 bits.
3. Les groupes binaires sont affichés à l'écran, séparés par des espaces.
4. Le joueur tape le mot décodé dans le champ de texte.
5. `checkAnswer()` compare l'entrée (nettoyée + en majuscules) avec le mot cible.

**Pour ajouter un nouveau mot binaire :**
1. Ajouter le mot à la liste dans `BinaryPuzzle.kt`
2. Ajouter une entrée `QuizConfig` dans `logic/WordQuizData.kt` (pour `juniorWordQuizMap` et `seniorWordQuizMap`)
3. Ajouter toutes les chaînes de ressources dans les trois fichiers `strings.xml` (EN, FR, NL)

---

## 7. Système audio (bulles de parole)

Chaque écran de jeu a une **bulle de parole IA** — un avatar vidéo circulaire qui joue en même temps qu'un MP3 de narration.

**Comment ça fonctionne :**
- La vidéo (`ai_speech_bubble.mp4` en boucle) est rendue par `AISpeechBubble.kt` avec ExoPlayer.
- L'audio (`.mp3` spécifique à la locale) est joué par un `MediaPlayer` dans le composable de l'écran.
- Quand l'audio se termine, `isBubblePlaying` passe à `false` — la bulle affiche une icône play.
- Appuyer sur la bulle bascule pause/play.
- Sur les écrans où l'audio doit finir avant de continuer, le bouton est `enabled = hasFinishedAudio`.

**Convention de nommage des fichiers audio :**
```
audio/[écran]_[locale].mp3
audio/[écran]_[niveau]_[locale].mp3
```

**Chaîne de fallback :** Si `binary_fr.mp3` est manquant, l'app essaie `test.mp3`. Si celui-là aussi est manquant, l'audio est ignoré silencieusement.

**Pour ajouter l'audio en anglais ou néerlandais :**
Ajouter simplement `binary_en.mp3`, `binary_nl.mp3`, `scratch_en.mp3`, etc. dans `app/src/main/assets/audio/`. L'app récupère automatiquement le bon fichier selon la langue active.

---

## 8. Système vidéo

Les vidéos sont gérées par `logic/VideoAssetManager.kt`.

**Les fichiers vidéo vont dans :** `app/src/main/assets/videos/`

| Fichier | Écran | Notes |
|---|---|---|
| `intro.mp4` | `VideoScreen.kt` | Joue avant la sélection de difficulté. Si manquant, affiche un placeholder. |
| `outro.mp4` | `OutroScreen.kt` | Joue après toutes les missions. |
| `ai_speech_bubble.mp4` | `AISpeechBubble.kt` | Avatar animé en boucle. Joue sur les écrans Binaire, Scratch, IA et Robot. |

**Format recommandé : MP4, H.264, AAC.** Gardez les tailles raisonnables — les grandes vidéos augmentent considérablement la taille de l'APK.

---

## 9. Système de langues / localisation

L'application utilise le système de ressources de chaînes intégré d'Android avec `AppCompatDelegate.setApplicationLocales()` pour changer la langue à l'exécution.

**Comment le changement de langue fonctionne :**
1. Le joueur appuie sur le 🌐 globe sur l'écran d'accueil.
2. `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("fr"))` est appelé.
3. Android recrée l'Activity avec la nouvelle locale — tous les appels `stringResource()` retournent maintenant les chaînes françaises.
4. La sélection persiste entre les redémarrages de l'app.

**Pour ajouter une nouvelle langue (ex. allemand) :**
1. Créer `res/values-de/strings.xml`
2. Copier toutes les clés depuis `res/values/strings.xml`
3. Traduire toutes les valeurs
4. Ajouter `"de"` à la liste `languages` dans `HomeScreen.kt`
5. Ajouter `"de" to "DE"` dans `languageLabels` dans `HomeScreen.kt`
6. Ajouter les fichiers audio : `binary_de.mp3`, `scratch_de.mp3`, etc.

---

## 10. Référence écran par écran

### `HomeScreen.kt`
- Point d'entrée. Le joueur appuie n'importe où pour continuer.
- Contient le sélecteur de langue (bouton globe en haut à droite).
- La chaîne `home_tap_to_start` est le texte blanc clignotant en bas.

### `DifficultyScreen.kt`
- Deux cartes d'agent (Junior / Senior), chacune avec des listes de fonctionnalités.
- Sélectionner une carte révèle le bouton "CONFIRMER — AGENT JUNIOR/SENIOR".
- Le label du bouton utilise `difficulty_confirm` + `btn_kids`/`btn_teens` (entièrement traduit).

### `BinaryGameScreen.kt`
- Affiche la chaîne binaire encodée depuis `BinaryPuzzle` immédiatement — il n'y a pas de bouton de démarrage.
- Barre supérieure : bouton home, indicateur de progression des 4 missions, minuterie.
- La bulle de parole joue `binary_[locale].mp3`. L'audio démarre automatiquement. **Rien n'est bloqué sur la fin de l'audio** — le puzzle est toujours interactif.
- Soumettre une bonne réponse navigue vers `binary_quiz/{word}`.

### `ExternalGameScreen.kt`
- Utilisé pour Scratch (étape 2) et Robot (étape 4).
- Affiche : label mission, titre, logo Scratch (seulement étape 2), bulle de parole + instructions, bouton verrou.
- Le joueur appuie sur le verrou pour révéler le champ de saisie du code.
- La bulle joue `scratch_[locale].mp3` ou `robot_[locale].mp3`.

### `AiGameScreen.kt` (Junior uniquement)
- Trois phases : `INTRO → COUNTDOWN → GAME`.
- **INTRO :** Bulle joue `ai_junior_[locale].mp3`. Affiche 4 lignes d'instructions. Bouton démarrer désactivé jusqu'à la fin de l'audio.
- **COUNTDOWN :** Affiche 3... 2... 1... GO! en plein écran.
- **GAME :** 5 tours de repérage d'icônes. La vitesse augmente à chaque tour.

### `AiValidationScreen.kt` (Senior uniquement)
- Flux de validation micro:bit multi-phases.
- **INTRO :** Bulle joue `ai_senior_[locale].mp3`. Bouton démarrer désactivé jusqu'à la fin de l'audio.
- **BAD_PREDICTION :** Révélation en 2 étapes. D'abord le panneau d'erreur + bouton "Continuer". Ensuite le diagnostic + bouton "Recalibrer".
- **CODE_INPUT_2 :** Seulement le bon code (`config.aiCode`) passe à SUCCESS.

---

## 11. Comment modifier les choses

### Changer un code d'accès

**Fichier :** `logic/MissionConfig.kt`

```kotlin
val JUNIOR_AGENT = MissionConfig(
    scratchCode = "LOOP",   // ← changer pour n'importe quelle chaîne en majuscules
    aiCode      = "AI",
    robotCode   = "ROBOT",
    ...
)
```

---

### Changer la durée de la minuterie

```kotlin
binaryTimerSeconds = 10 * 60   // 10 minutes → changer par ex. 8 * 60 pour 8 minutes
```

---

### Changer le texte d'une question de quiz

**Fichiers :** `res/values/strings.xml`, `res/values-fr/strings.xml`, `res/values-nl/strings.xml`

Trouver la clé de chaîne et modifier le texte. Toujours modifier les **trois fichiers de langue**.

---

### Changer quelle option est correcte

**Fichier :** `logic/MissionConfig.kt` (Scratch/IA/Robot) ou `logic/WordQuizData.kt` (Binaire)

Changer `correctIndex` :
- `0` = l'option A est correcte
- `1` = l'option B est correcte
- `2` = l'option C est correcte

> ⚠️ **Important :** Actuellement **tous les quiz** dans `WordQuizData.kt` et `MissionConfig.kt` ont `correctIndex = 0`, ce qui signifie que **l'option A est toujours la bonne réponse**. Si vous ajoutez un nouveau quiz où l'option B ou C est correcte, vous **devez** changer `correctIndex` en `1` ou `2` — sinon l'option A sera marquée correcte peu importe le texte.

---

### Ajouter ou mettre à jour l'audio

Déposer le fichier MP3 dans `app/src/main/assets/audio/` avec le bon nom :
```
[écran]_[locale].mp3
```
Par exemple : `binary_en.mp3`, `scratch_nl.mp3`. Aucune modification de code n'est nécessaire.

---

## 12. Comment ajouter une nouvelle mission

L'ajout d'une mission nécessite des modifications à 4 endroits :

1. **`navigation/NavGraph.kt`** — ajouter une route et un bloc `composable()`
2. **Navigation** — changer le `onContinue` de l'écran précédent pour pointer vers la nouvelle route
3. **`screens/`** — créer un nouveau composable d'écran
4. **`logic/MissionConfig.kt`** — ajouter un `QuizConfig` pour le quiz de la nouvelle mission, et ajouter les chaînes dans les trois fichiers `strings.xml`

---

## 13. Carte des fichiers

```
logic/
  MissionConfig.kt        ← COMMENCER ICI pour toute la config spécifique à la difficulté
  QuizConfig.kt           ← Classe de données : question + 3 options + explication
  WordQuizData.kt         ← Cartes de quiz de mots binaires (junior + senior)
  BinaryPuzzle.kt         ← Génération du puzzle binaire et vérification des réponses
  VideoAssetManager.kt    ← Charge les fichiers vidéo depuis assets/

viewmodel/
  GameViewModel.kt        ← Stocke la difficulté, l'heure de début, expose missionConfig

navigation/
  NavGraph.kt             ← Flux complet des missions / toutes les routes

screens/
  HomeScreen.kt           ← Écran d'accueil avec sélecteur de langue
  VideoScreen.kt          ← Vidéo d'intro (intro.mp4)
  DifficultyScreen.kt     ← Sélection Agent Junior / Senior
  BinaryGameScreen.kt     ← Mission 1 : Décodeur Binaire
  QuizScreen.kt           ← Quiz générique (utilisé après chaque mission)
  ExternalGameScreen.kt   ← Écrans de saisie de code Scratch (M2) + Robot (M4)
  AiGameScreen.kt         ← Mission 3 Junior : jeu de repérage d'icônes
  AiValidationScreen.kt   ← Mission 3 Senior : validation IA micro:bit
  AISpeechBubble.kt       ← Composable vidéo circulaire + audio
  CongratulationsScreen.kt← Écran Mission Accomplie
  HomeButton.kt           ← Bouton home réutilisable avec dialogue de confirmation

theme/
  Color.kt                ← Palette de couleurs complète
  Type.kt                 ← Échelle typographique (agrandie pour tablettes)
  Theme.kt                ← EscapeGameTheme (thème sombre uniquement)

res/values/strings.xml    ← Textes anglais + tout le contenu des quiz
res/values-fr/strings.xml ← Traductions françaises
res/values-nl/strings.xml ← Traductions néerlandaises

assets/audio/             ← Fichiers MP3 ([écran]_[locale].mp3)
assets/videos/            ← Fichiers MP4 (intro, outro, ai_speech_bubble)
```

---

## 14. Référence du contenu des quiz

### Principe
> Chaque quiz est lié au code ou au mot que le joueur vient d'utiliser.
> Décoder `SERVER` → quiz sur les serveurs. Taper `LOOP` → quiz sur les boucles.

### Quiz des mots binaires

| Mot | Question Junior | Question Senior |
|---|---|---|
| DATA | Qu'est-ce que la donnée ? | Différence entre données et information ? |
| CODE | Qu'est-ce que le code en programmation ? | Quel est le rôle d'une fonction ? |
| SERVER | Qu'est-ce qu'un serveur ? | Dans un modèle client-serveur, que fait le client ? |
| ROBOT | Comment un robot sait-il quoi faire ? | Que signifie "autonome" pour un robot ? |
| CLOUD | C'est quoi "le cloud" ? | Avantage principal du cloud pour un datacenter ? |

### Quiz des missions

| Mission | Question Junior | Question Senior |
|---|---|---|
| Scratch | À quoi sert Scratch ? | Que représente un bloc "répéter" ? |
| IA | Que signifie IA ? | Comment un modèle ML améliore-t-il ses performances ? |
| Robot | Comment un robot sait-il quoi faire ? | Capteur vs actionneur — quelle différence ? |