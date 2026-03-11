# CodeNPlay — Developer Guide

## What is this app?

A tablet-based escape room mission app for kids and teens at a datacenter event.
Players complete 4 missions (Binary Decoder → Scratch → AI → Robot) and finish with a congratulations screen.

---

## Mission codes & answers (quick reference)

### Mission 1 — Binary Decoder

Players decode a binary string into a word. The word is **random** each run, chosen from:

| Binary word | Correct answer | Quiz topic after solving |
|---|---|---|
| DATA | `DATA` | What is data? |
| CODE | `CODE` | What is source code / what is a function? |
| SERVER | `SERVER` | What is a server / client-server model? |
| ROBOT | `ROBOT` | How does a robot work / what is autonomy? |
| CLOUD | `CLOUD` | What is the cloud / cloud computing advantages? |

The quiz that appears **immediately after** Mission 1 is determined by the word the player decoded. If they decoded `SERVER`, they get the server quiz. This is automatic — no configuration needed.

> The exact quiz question and options depend on difficulty (kids = simpler, teens = harder). See the "Quiz difficulty at a glance" table below.

---

### Mission 2 — Scratch Protocol

| | Code to enter | Quiz topic after |
|---|---|---|
| Junior Agent (Kids) | `LOOP` | What is Scratch used for? |
| Senior Agent (Teens) | `LOOP` | What does a 'repeat' block represent in Scratch? |

The quiz is about the **word they just typed** — `LOOP` — so it's a loop / Scratch question.

---

### Mission 3 — AI Protocol

| | Code to enter | Quiz topic after |
|---|---|---|
| Junior Agent (Kids) | `AI` | What does AI stand for? |
| Senior Agent (Teens) | `AI` | How does a machine learning model improve? |

The quiz is about the **word they just typed** — `AI` — so it's an AI question.

---

### Mission 4 — Robot Protocol

| | Code to enter | Quiz topic after | Robot station |
|---|---|---|---|
| Junior Agent (Kids) | `ROBOT` | How does a robot know what to do? | Ozobot |
| Senior Agent (Teens) | `ROBOT` | Sensor vs actuator — what's the difference? | LEGO Spike |

The quiz is about the **word they just typed** — `ROBOT` — so it's a robotics question.

---

### The pattern

> **Every quiz is about the code or word the player just entered.**
> Decode `SERVER` → quiz about servers. Type `LOOP` → quiz about loops. Type `AI` → quiz about AI.
> This connects the hands-on activity directly to the learning question that follows it.

To change what quiz follows a mission, change the code players type in (`MissionConfig.kt`) and add a matching quiz for that word (`WordQuizData.kt` for Mission 1, or `scratchQuiz`/`aiQuiz`/`robotQuiz` in `MissionConfig.kt` for Missions 2–4).

---

## How difficulty works

There are two difficulty levels, each with its own configuration object:

| Level | Label | Who |
|---|---|---|
| `JUNIOR_AGENT` | Junior Agent | Kids |
| `SENIOR_AGENT` | Senior Agent | Teens |

**Everything that differs between kids and teens is defined in one file:**

```
app/src/main/java/com/example/escapegame/logic/MissionConfig.kt
```

Open that file first. It contains two clearly labelled config blocks — `JUNIOR_AGENT` and `SENIOR_AGENT`.

---

## Where to change things

### Change access codes (the codes players type in)

**File:** `logic/MissionConfig.kt`

Edit `scratchCode`, `aiCode`, or `robotCode` inside `JUNIOR_AGENT` or `SENIOR_AGENT`:

```kotlin
val JUNIOR_AGENT = MissionConfig(
    scratchCode = "LOOP",   // ← change this
    aiCode      = "AI",     // ← or this
    robotCode   = "ROBOT",  // ← or this
    ...
)
```

Both difficulties can have different codes, or the same — your choice.

---

### Change quiz questions, options, or explanations

**For text content:** edit the string resource files — one section per difficulty:

```
app/src/main/res/values/strings.xml        (English)
app/src/main/res/values-nl/strings.xml     (Dutch)
app/src/main/res/values-fr/strings.xml     (French)
```

Each file has two clearly labelled sections at the bottom:

```xml
<!-- JUNIOR AGENT (Kids) quiz strings — edit here for kids -->
...
<!-- SENIOR AGENT (Teens) quiz strings — edit here for teens -->
...
```

- `_kids` suffix → shown to Junior Agent players
- `_teens` suffix → shown to Senior Agent players

**For which string a quiz uses** (if you add new string keys):

Edit `logic/MissionConfig.kt` — each quiz stage (`scratchQuiz`, `aiQuiz`, `robotQuiz`) is a `QuizConfig` block pointing to string resource IDs.

**For the binary word quizzes** (decoded words like DATA, CODE, SERVER):

Edit the word quiz maps in `logic/WordQuizData.kt`:

```kotlin
val juniorWordQuizMap = ...   // Kids word quizzes
val seniorWordQuizMap = ...   // Teens word quizzes
```

---

### Change the timer

**File:** `logic/MissionConfig.kt`

```kotlin
val JUNIOR_AGENT = MissionConfig(
    binaryTimerSeconds = 15 * 60,  // ← 15 minutes for kids
    ...
)
val SENIOR_AGENT = MissionConfig(
    binaryTimerSeconds = 10 * 60,  // ← 10 minutes for teens
    ...
)
```

---

### Change robot station instructions

**File:** `logic/MissionConfig.kt`

```kotlin
val JUNIOR_AGENT = MissionConfig(
    robotInstructionsRes = R.string.robot_instructions_kids,  // ← Ozobot
    ...
)
val SENIOR_AGENT = MissionConfig(
    robotInstructionsRes = R.string.robot_instructions_teens, // ← LEGO Spike
    ...
)
```

Edit the actual instruction text in the strings files under `robot_instructions_kids` / `robot_instructions_teens`.

---

### Change UI design per difficulty (button sizes, colors, layout)

The difficulty style is passed into every screen as `uiStyle: AgentUiStyle` (either `JUNIOR` or `SENIOR`).

**Where to add visual differences:**

| File | What it controls |
|---|---|
| `screens/QuizScreen.kt` | Quiz button height (`buttonHeight`) |
| `screens/BinaryGameScreen.kt` | Binary puzzle screen |
| `screens/ExternalGameScreen.kt` | Scratch / AI / Robot entry screens |
| `screens/CongratulationsScreen.kt` | End screen |
| `theme/Type.kt` | Font sizes (currently shared) |
| `theme/Color.kt` | Colors (currently shared) |

**Example pattern** (already used in QuizScreen):

```kotlin
val buttonHeight = if (uiStyle == AgentUiStyle.JUNIOR) 72.dp else 64.dp
```

To add more per-difficulty UI, pass `uiStyle` into the relevant screen and apply the same pattern.

---

## File map

```
logic/
  MissionConfig.kt      ← START HERE for all difficulty-specific config
  QuizConfig.kt         ← Data class for a quiz stage (question + 3 options + explanation)
  WordQuizData.kt       ← Binary word quiz maps (junior + senior)
  BinaryPuzzle.kt       ← Binary puzzle generation and answer checking

viewmodel/
  GameViewModel.kt      ← Stores selected difficulty, exposes missionConfig

navigation/
  NavGraph.kt           ← Screen flow; reads everything from viewModel.missionConfig

screens/
  HomeScreen.kt         ← Landing screen (tap to start)
  VideoScreen.kt        ← Mission briefing video
  DifficultyScreen.kt   ← Junior Agent / Senior Agent selection
  BinaryGameScreen.kt   ← Mission 1: Binary Decoder
  QuizScreen.kt         ← Generic debrief quiz (used after every mission)
  ExternalGameScreen.kt ← Generic external activity screen (Scratch / AI / Robot)
  CongratulationsScreen.kt ← Mission Complete screen

theme/
  MissionControlBackground.kt ← Dark background with cyan grid lines
  Color.kt              ← Mission control color palette
  Type.kt               ← Font sizes (enlarged for tablet)
  Theme.kt              ← EscapeGameTheme (single fixed dark theme)

res/values/             ← English strings
res/values-nl/          ← Dutch strings
res/values-fr/          ← French strings
```

---

## Quiz difficulty at a glance

| Quiz | Junior (Kids) | Senior (Teens) |
|---|---|---|
| Binary: DATA | What is data? | Data vs information — what's the difference? |
| Binary: CODE | What is code? | What is the purpose of a function? |
| Binary: SERVER | What is a server? | In a client-server model, what does the client do? |
| Binary: ROBOT | How does a robot know what to do? | What does 'autonomous' mean for a robot? |
| Binary: CLOUD | What is the cloud? | Key advantage of cloud computing for a datacenter? |
| Scratch | What is Scratch used for? | What does a 'repeat' block represent? |
| AI | What does AI stand for? | How does a machine learning model improve? |
| Robot | How does a robot know what to do? | Sensor vs actuator — what's the difference? |

---

## Language support

The app supports EN / NL / FR, switchable from the home screen.
All text lives in the three `strings.xml` files. The app re-renders automatically when the language is changed.

To add a new language: create `res/values-XX/strings.xml` and copy all keys from the EN file.
