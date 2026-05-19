# CodeNPlay — Escape Room Mission App

> 🇬🇧 English documentation below — 🇫🇷 La documentation française suit en dessous

---

# 🇬🇧 ENGLISH

## What is EscapeGame?

EscapeGame is an Android tablet app that turns coding education into an escape room experience. Players become "agents" who must complete 4 coding missions to save a hacked datacenter. Each mission teaches a real computer science concept through gameplay, followed by a quiz.

The app is designed for **tablets in landscape mode** and supports **3 languages**: English, Dutch, and French. The language can be switched at any time from the home screen.

---

## Quick Start — First Test (APK)

The app has been built and is ready to install on tablets.

**APK file location (on the build computer):**
```
app/build/outputs/apk/debug/app-debug.apk
```
**File size:** ~73 MB

### Steps to install on a tablet

1. **Upload the APK to Google Drive** (or any shared storage: USB stick, email, etc.)
2. **On each tablet**, open Google Drive and download the APK file
3. **Before installing**, enable "Install unknown apps":
   - Go to **Settings → Apps → Special app access → Install unknown apps**
   - Select the app you'll use to open the file (Files, Chrome, Drive…) and toggle **Allow**
4. **Open the downloaded APK** file and tap **Install**
5. The app appears as **"EscapeGame"** in the launcher

> ⚠️ You must enable "Install unknown apps" on **each tablet individually** before the APK can be installed.

---

## Game Flow

```
Home Screen  (tap anywhere to start)
  ↓
Intro Video  (briefing video)
  ↓
Difficulty Selection  (Junior Agent / Senior Agent)
  ↓
Mission 1 — Binary Decoder  (timed puzzle)
  ↓
Binary Quiz
  ↓
Mission 2 — Scratch Protocol  (go to laptop, enter code: POPCORN)
  ↓
Scratch Quiz
  ↓
Mission 3 — AI Protocol
  Junior: icon-spotting mini-game  (enter code: AI)
  Senior: micro:bit AI validation  (enter code: PROMPT)
  ↓
AI Quiz
  ↓
Mission 4 — Robot Protocol
  Junior: Ozobot station  (enter code: 578)
  Senior: LEGO Spike station  (enter code: 578)
  ↓
Robot Quiz
  ↓
Congratulations Screen
```

---

## Difficulty Levels

| | Junior Agent (Kids) | Senior Agent (Teens) |
|---|---|---|
| **Timer** | 10 minutes | 5 minutes |
| **Hints** | Available | None |
| **AI Mission** | Icon-spotting game | micro:bit AI validation |
| **Robot Station** | Ozobot | LEGO Spike |
| **Quiz level** | Conceptual, age-appropriate | More technical, deeper questions |

---

## Access Codes (for game masters)

These codes are entered by players to unlock each mission. They are defined in `logic/MissionConfig.kt`.

| Mission | Junior Code | Senior Code |
|---|---|---|
| Binary Decoder | *(solved by decoding binary)* | *(solved by decoding binary)* |
| Scratch Protocol | `POPCORN` | `POPCORN` |
| AI Protocol | `AI` | `PROMPT` |
| Robot Protocol | `578` | `578` |

---

## Audio Files

Audio plays automatically on each screen and drives the speech bubble animation. Files go in:
```
app/src/main/assets/audio/
```

| File | Screen |
|---|---|
| `binary_fr.mp3` / `binary_en.mp3` / `binary_nl.mp3` | Binary puzzle screen |
| `scratch_fr.mp3` / `scratch_en.mp3` / `scratch_nl.mp3` | Scratch mission screen |
| `ai_junior_fr.mp3` / `ai_junior_en.mp3` / `ai_junior_nl.mp3` | AI game (Junior) intro |
| `ai_senior_fr.mp3` / `ai_senior_en.mp3` / `ai_senior_nl.mp3` | AI validation (Senior) intro |
| `robot_fr.mp3` / `robot_en.mp3` / `robot_nl.mp3` | Robot mission screen |

> All language versions (`_en.mp3`, `_nl.mp3`, `_fr.mp3`) are present. If a language file is ever missing, the app silently skips audio.

---

## Video Files

Videos go in:
```
app/src/main/assets/videos/
```

| File | Description |
|---|---|
| `intro.mp4` | Plays before difficulty selection |
| `outro.mp4` | Plays after all missions on the congratulations screen |
| `ai_speech_bubble.mp4` | Looping animated AI avatar shown with the speech bubble |

> Files must be **MP4 format** (H.264 + AAC recommended). Keep sizes reasonable for tablet storage.

---

## How to Change Game Content

### Change access codes or timers
Edit `app/src/main/java/com/example/escapegame/logic/MissionConfig.kt`:
- `scratchCode`, `aiCode`, `robotCode` — the codes players must enter
- `binaryTimerSeconds` — timer for the binary puzzle (in seconds)

### Change quiz questions / text
Edit `app/src/main/res/values/strings.xml` (English), `values-fr/strings.xml` (French), or `values-nl/strings.xml` (Dutch).
- Junior quiz strings end in `_kids`
- Senior quiz strings end in `_teens`

---

## Installing the App on a Tablet

### Method 1 — Android Studio (recommended for development)

**Requirements:**
- Android Studio (latest version)
- JDK 11 or higher
- A tablet with Android 7.0 (API 24) or higher
- A USB cable

**Steps:**

1. **Open the project** in Android Studio (`File → Open → select the CodeNPlay folder`)

2. **Enable Developer Mode on the tablet:**
   - Go to `Settings → About tablet`
   - Tap **Build number** 7 times until you see "You are now a developer"
   - Go back to `Settings → Developer options`
   - Enable **USB debugging**

3. **Connect the tablet via USB.** Android Studio will detect it. Accept the "Allow USB debugging" prompt on the tablet.

4. **Select the tablet** from the device dropdown at the top of Android Studio.

5. **Click the green Run button ▶** or run in the terminal:
   ```bash
   ./gradlew installDebug
   ```

6. The app installs and opens automatically on the tablet.

---

### Method 2 — APK file (no computer needed after build)

This is the best method if you want to install on multiple tablets without plugging each one into a computer.

**Step 1 — Build the APK:**

In the terminal (inside the project folder):
```bash
./gradlew assembleDebug
```
This creates the file at:
```
app/build/outputs/apk/debug/app-debug.apk
```

Or in Android Studio: `Build → Build Bundle(s) / APK(s) → Build APK(s)`, then click "locate" to find the file.

**Step 2 — Transfer the APK to the tablet:**

Option A — USB:
- Copy `app-debug.apk` to the tablet via USB (drag to the tablet's Downloads folder)

Option B — Google Drive / email:
- Upload the APK to Google Drive or send by email, then open it on the tablet

**Step 3 — Allow installation from unknown sources:**
- On the tablet: `Settings → Apps → Special app access → Install unknown apps`
- Find your file manager or browser and enable **"Allow from this source"**

**Step 4 — Install:**
- Open the file manager on the tablet
- Navigate to Downloads (or wherever you copied the APK)
- Tap `app-debug.apk`
- Tap **Install**
- Tap **Open** when done

---

### Method 3 — ADB command line (fast for multiple tablets)

If you have ADB installed and the tablet has USB debugging enabled:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

To install on all connected tablets at once:
```bash
adb devices | grep -v "List" | cut -f1 | xargs -I{} adb -s {} install app-debug.apk
```

---

## Setting the Language on a Tablet

The language is changed **inside the app** from the home screen:

1. Open CodeNPlay
2. Tap the 🌐 globe icon in the **top-right corner**
3. Select **EN**, **NL**, or **FR**
4. The app switches language immediately — no restart needed

> The language setting is saved by Android and persists when the app is closed and reopened.

---

## Project File Structure

```
CodeNPlay/
├── app/src/main/
│   ├── java/com/example/escapegame/
│   │   ├── logic/
│   │   │   ├── MissionConfig.kt     ← access codes, timers, difficulty settings
│   │   │   ├── BinaryPuzzle.kt      ← binary encoding/decoding logic
│   │   │   ├── WordQuizData.kt      ← quiz questions per binary word
│   │   │   └── QuizConfig.kt        ← quiz data structure
│   │   ├── screens/                 ← all UI screens (one file per screen)
│   │   ├── navigation/NavGraph.kt   ← full mission flow / routing
│   │   ├── viewmodel/               ← game state (difficulty, timer)
│   │   ├── theme/                   ← colors, fonts, background
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── values/strings.xml       ← English text + quiz content
│   │   ├── values-fr/strings.xml    ← French translations
│   │   └── values-nl/strings.xml    ← Dutch translations
│   └── assets/
│       ├── audio/                   ← MP3 audio files per screen/language
│       └── videos/                  ← MP4 video files (intro, outro, speech bubble)
└── gradle/libs.versions.toml        ← dependency versions
```

---
---

# 🇫🇷 FRANÇAIS

## Qu'est-ce que EscapeGame?

EscapeGame est une application Android pour tablettes qui transforme l'apprentissage de la programmation en jeu d'évasion. Les joueurs incarnent des "agents" qui doivent accomplir 4 missions de codage pour sauver un datacenter piraté. Chaque mission enseigne un vrai concept informatique à travers le jeu, suivi d'un quiz.

L'application est conçue pour les **tablettes en mode paysage** et supporte **3 langues** : anglais, néerlandais et français. La langue peut être changée à tout moment depuis l'écran d'accueil.

---

## Démarrage rapide — Premier test (APK)

L'application a été compilée et est prête à être installée sur les tablettes.

**Emplacement du fichier APK (sur l'ordinateur de compilation) :**
```
app/build/outputs/apk/debug/app-debug.apk
```
**Taille du fichier :** ~73 Mo

### Étapes pour installer sur une tablette

1. **Téléverser l'APK sur Google Drive** (ou tout autre stockage partagé : clé USB, e-mail, etc.)
2. **Sur chaque tablette**, ouvrir Google Drive et télécharger le fichier APK
3. **Avant l'installation**, activer « Installer des applis inconnues » :
   - Aller dans **Paramètres → Applications → Accès spécial → Installer des applis inconnues**
   - Sélectionner l'application utilisée pour ouvrir le fichier (Fichiers, Chrome, Drive…) et activer l'option
4. **Ouvrir le fichier APK** téléchargé et appuyer sur **Installer**
5. L'application apparaît sous le nom **"EscapeGame"** dans le lanceur

> ⚠️ L'option « Installer des applis inconnues » doit être activée **sur chaque tablette individuellement** avant de pouvoir installer l'APK.

---

## Déroulement du jeu

```
Écran d'accueil  (appuyer n'importe où pour commencer)
  ↓
Vidéo d'introduction  (briefing de mission)
  ↓
Choix de difficulté  (Agent Junior / Agent Senior)
  ↓
Mission 1 — Décodeur Binaire  (puzzle chronométré)
  ↓
Quiz Binaire
  ↓
Mission 2 — Protocole Scratch  (aller au laptop, entrer le code : POPCORN)
  ↓
Quiz Scratch
  ↓
Mission 3 — Protocole IA
  Junior : mini-jeu de repérage d'icônes  (entrer le code : AI)
  Senior : validation IA avec micro:bit   (entrer le code : PROMPT)
  ↓
Quiz IA
  ↓
Mission 4 — Protocole Robot
  Junior : station Ozobot   (entrer le code : 578)
  Senior : station LEGO Spike  (entrer le code : 578)
  ↓
Quiz Robot
  ↓
Écran de félicitations
```

---

## Niveaux de difficulté

| | Agent Junior (Enfants) | Agent Senior (Ados) |
|---|---|---|
| **Minuterie** | 10 minutes | 5 minutes |
| **Indices** | Disponibles | Aucun |
| **Mission IA** | Jeu de repérage d'icônes | Validation IA micro:bit |
| **Station Robot** | Ozobot | LEGO Spike |
| **Niveau des quiz** | Conceptuel, adapté à l'âge | Plus technique, questions approfondies |

---

## Codes d'accès (pour les game masters)

Ces codes sont saisis par les joueurs pour débloquer chaque mission. Ils sont définis dans `logic/MissionConfig.kt`.

| Mission | Code Junior | Code Senior |
|---|---|---|
| Décodeur Binaire | *(résolu en décodant le binaire)* | *(résolu en décodant le binaire)* |
| Protocole Scratch | `POPCORN` | `POPCORN` |
| Protocole IA | `AI` | `PROMPT` |
| Protocole Robot | `578` | `578` |

---

## Fichiers Audio

L'audio se lance automatiquement sur chaque écran et anime la bulle de parole. Les fichiers vont dans :
```
app/src/main/assets/audio/
```

| Fichier | Écran |
|---|---|
| `binary_fr.mp3` / `binary_en.mp3` / `binary_nl.mp3` | Écran du puzzle binaire |
| `scratch_fr.mp3` / `scratch_en.mp3` / `scratch_nl.mp3` | Écran mission Scratch |
| `ai_junior_fr.mp3` / `ai_junior_en.mp3` / `ai_junior_nl.mp3` | Intro jeu IA (Junior) |
| `ai_senior_fr.mp3` / `ai_senior_en.mp3` / `ai_senior_nl.mp3` | Intro validation IA (Senior) |
| `robot_fr.mp3` / `robot_en.mp3` / `robot_nl.mp3` | Écran mission Robot |

> Toutes les versions linguistiques (`_en.mp3`, `_nl.mp3`, `_fr.mp3`) sont présentes. Si un fichier est manquant, l'application ignore silencieusement l'audio.

---

## Fichiers Vidéo

Les vidéos vont dans :
```
app/src/main/assets/videos/
```

| Fichier | Description |
|---|---|
| `intro.mp4` | Vidéo jouée avant la sélection de difficulté |
| `outro.mp4` | Vidéo jouée après toutes les missions |
| `ai_speech_bubble.mp4` | Avatar IA animé en boucle pour la bulle de parole |

> Les fichiers doivent être au format **MP4** (H.264 + AAC recommandé). Gardez les tailles raisonnables pour le stockage de la tablette.

---

## Comment modifier le contenu du jeu

### Changer les codes d'accès ou les minuteries
Modifier `app/src/main/java/com/example/escapegame/logic/MissionConfig.kt` :
- `scratchCode`, `aiCode`, `robotCode` — les codes que les joueurs doivent entrer
- `binaryTimerSeconds` — minuterie du puzzle binaire (en secondes)

### Changer les questions de quiz / le texte
Modifier `app/src/main/res/values/strings.xml` (anglais), `values-fr/strings.xml` (français) ou `values-nl/strings.xml` (néerlandais).
- Les chaînes Junior se terminent par `_kids`
- Les chaînes Senior se terminent par `_teens`

---

## Installer l'application sur une tablette

### Méthode 1 — Android Studio (recommandé pour le développement)

**Prérequis :**
- Android Studio (version la plus récente)
- JDK 11 ou supérieur
- Une tablette sous Android 7.0 (API 24) ou supérieur
- Un câble USB

**Étapes :**

1. **Ouvrir le projet** dans Android Studio (`Fichier → Ouvrir → sélectionner le dossier CodeNPlay`)

2. **Activer le mode développeur sur la tablette :**
   - Aller dans `Paramètres → À propos de la tablette`
   - Appuyer **7 fois sur Numéro de build** jusqu'à voir "Vous êtes maintenant développeur"
   - Retourner dans `Paramètres → Options pour les développeurs`
   - Activer le **débogage USB**

3. **Connecter la tablette en USB.** Android Studio la détecte automatiquement. Accepter la demande "Autoriser le débogage USB" sur la tablette.

4. **Sélectionner la tablette** dans le menu déroulant des appareils en haut d'Android Studio.

5. **Cliquer sur le bouton vert ▶ Exécuter** ou lancer dans le terminal :
   ```bash
   ./gradlew installDebug
   ```

6. L'application s'installe et s'ouvre automatiquement sur la tablette.

---

### Méthode 2 — Fichier APK (sans ordinateur après la compilation)

C'est la meilleure méthode pour installer sur **plusieurs tablettes** sans les brancher toutes à l'ordinateur.

**Étape 1 — Compiler l'APK :**

Dans le terminal (dans le dossier du projet) :
```bash
./gradlew assembleDebug
```
Cela crée le fichier ici :
```
app/build/outputs/apk/debug/app-debug.apk
```

Ou dans Android Studio : `Build → Build Bundle(s) / APK(s) → Build APK(s)`, puis cliquer sur "locate" pour trouver le fichier.

**Étape 2 — Transférer l'APK sur la tablette :**

Option A — USB :
- Copier `app-debug.apk` sur la tablette via USB (glisser dans le dossier Téléchargements)

Option B — Google Drive / email :
- Uploader l'APK sur Google Drive ou l'envoyer par email, puis l'ouvrir sur la tablette

**Étape 3 — Autoriser l'installation depuis des sources inconnues :**
- Sur la tablette : `Paramètres → Applications → Accès spécial aux applications → Installer des applications inconnues`
- Trouver votre gestionnaire de fichiers ou navigateur et activer **"Autoriser depuis cette source"**

**Étape 4 — Installer :**
- Ouvrir le gestionnaire de fichiers sur la tablette
- Naviguer vers Téléchargements (ou là où vous avez copié l'APK)
- Appuyer sur `app-debug.apk`
- Appuyer sur **Installer**
- Appuyer sur **Ouvrir** une fois terminé

---

### Méthode 3 — Ligne de commande ADB (rapide pour plusieurs tablettes)

Si ADB est installé et que la tablette a le débogage USB activé :
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

Pour installer sur toutes les tablettes connectées en même temps :
```bash
adb devices | grep -v "List" | cut -f1 | xargs -I{} adb -s {} install app-debug.apk
```

---

## Changer la langue sur une tablette

La langue se change **dans l'application** depuis l'écran d'accueil :

1. Ouvrir CodeNPlay
2. Appuyer sur l'icône 🌐 globe en **haut à droite**
3. Sélectionner **EN**, **NL** ou **FR**
4. L'application change de langue immédiatement — pas de redémarrage nécessaire

> Le paramètre de langue est sauvegardé par Android et persiste lorsque l'application est fermée et rouverte.

---

## Structure des fichiers du projet

```
CodeNPlay/
├── app/src/main/
│   ├── java/com/example/escapegame/
│   │   ├── logic/
│   │   │   ├── MissionConfig.kt     ← codes d'accès, minuteries, paramètres de difficulté
│   │   │   ├── BinaryPuzzle.kt      ← logique d'encodage/décodage binaire
│   │   │   ├── WordQuizData.kt      ← questions de quiz par mot binaire
│   │   │   └── QuizConfig.kt        ← structure de données du quiz
│   │   ├── screens/                 ← tous les écrans UI (un fichier par écran)
│   │   ├── navigation/NavGraph.kt   ← flux complet des missions / routage
│   │   ├── viewmodel/               ← état du jeu (difficulté, minuterie)
│   │   ├── theme/                   ← couleurs, polices, arrière-plan
│   │   └── MainActivity.kt
│   ├── res/
│   │   ├── values/strings.xml       ← textes anglais + contenu des quiz
│   │   ├── values-fr/strings.xml    ← traductions françaises
│   │   └── values-nl/strings.xml    ← traductions néerlandaises
│   └── assets/
│       ├── audio/                   ← fichiers MP3 audio par écran/langue
│       └── videos/                  ← fichiers MP4 vidéo (intro, outro, bulle de parole)
└── gradle/libs.versions.toml        ← versions des dépendances
```
