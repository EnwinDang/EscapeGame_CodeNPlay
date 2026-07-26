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

1. **Open the project** in Android Studio.
2. **Connect the tablet** via USB and enable USB Debugging.
3. **Click the green Run button ▶**.

### Method 2 — APK file (no computer needed after build)

1. **Build the APK**: In Android Studio menu, go to **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
2. **Locate the file**: When finished, a popup appears in the **bottom-right corner**. Click **"locate"** to find `app-debug.apk`.
3. **Transfer**: Upload to Google Drive or use a USB stick.
4. **Install**: Open on the tablet (ensure "Install unknown apps" is enabled in settings).

---

# 🇫🇷 FRANÇAIS

## Qu'est-ce que EscapeGame?

EscapeGame est une application Android pour tablettes qui transforme l'apprentissage de la programmation en jeu d'évasion. Les joueurs incarnent des "agents" qui doivent accomplir 4 missions de codage pour sauver un datacenter piraté.

L'application est conçue pour les **tablettes en mode paysage** et supporte **3 langues** : anglais, néerlandais et français.

---

## Guide d'Installation de l'Application (APK)

C'est la méthode recommandée pour installer le jeu sur plusieurs tablettes.

### Étape 1 : Générer le fichier APK (Build)
1. Ouvrez le projet dans **Android Studio**.
2. Dans le menu supérieur, allez dans **Build** > **Build Bundle(s) / APK(s)** > **Build APK(s)**.
3. Attendez la fin du processus. Une petite notification apparaîtra in de **coin inférieur droit**.
4. Cliquez sur le lien bleu **"locate"** dans cette notification pour ouvrir le dossier contenant le fichier `app-debug.apk`.
   *Si vous manquez la notification, le fichier se trouve ici :* `app/build/outputs/apk/debug/app-debug.apk`

### Étape 2 : Transférer le fichier sur la tablette
*   **Option A (Google Drive) :** Téléchargez le fichier `app-debug.apk` sur votre Google Drive, puis ouvrez l'application Drive sur la tablette pour le télécharger.
*   **Option B (Clé USB) :** Copiez le fichier sur une clé USB et branchez-la sur la tablette.

### Étape 3 : Installer sur la tablette
1. **Autoriser les sources inconnues** : Sur la tablette, allez dans **Paramètres** > **Applications** > **Accès spécial** > **Installer des applications inconnues**. Activez l'autorisation pour l'application que vous utilisez (par exemple 'Fichiers' ou 'Drive').
2. **Lancer l'installation** : Ouvrez le fichier `app-debug.apk` sur la tablette et appuyez sur **Installer**.
3. Une fois terminé, l'icône "CodeNPlay" apparaîtra dans votre liste d'applications.

---

## Déroulement du jeu

```
Écran d'accueil
  ↓
Vidéo d'introduction
  ↓
Choix de difficulté (Junior / Senior)
  ↓
Missions 1 à 4 (Binaire, Scratch, IA, Robot)
  ↓
Écran de félicitations
```

---

## Fichiers Audio et Vidéo
Les fichiers se trouvent dans `app/src/main/assets/`. Pour ajouter une nouvelle langue, suivez la nomenclature `nom_langue.mp3` (ex: `intro_fr.mp3`).
