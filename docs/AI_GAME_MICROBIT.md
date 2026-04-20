# AI Mission — micro:bit Setup & Game Logic

This document covers everything needed to set up the micro:bit for the Senior Agent AI mission, understand how it works, and run the game without issues.

---

## 1. Download the micro:bit Code

**MakeCode project link:**
[https://makecode.microbit.org/_VM8WJvU4g1Ug](https://makecode.microbit.org/_VM8WJvU4g1Ug)

### Steps to flash the micro:bit

1. Open the link above in a browser
2. Click **Download** (bottom left of the MakeCode editor)
3. Connect the micro:bit to a computer via USB — it appears as a USB drive called `MICROBIT`
4. Drag and drop the downloaded `.hex` file onto the `MICROBIT` drive
5. The micro:bit will flash briefly, then restart and show `AI`
6. It is now ready to use


---

## 2. Controls

| Action | micro:bit shows | Input |
|---|---|---|
| Start test sequence | — | Press **A+B** simultaneously |
| Start training | — | Press **B** |
| Tilt left | ← arrow | = **L** |
| Tilt right | → arrow | = **R** |
| Shake | ↑ arrow | = **U** |

---

## 3. Test Sequence

The fixed sequence the player must execute during both runs:

```
L → R → U → L → U → R
```

The micro:bit shows each arrow one by one, then **GO** — the player then performs the movements in that exact order.

---

## 4. Training Sequence

The sequence shown during recalibration (9 steps):

```
L → R → U → L → R → U → L → R → U
```

The model is considered **good** when the player scores at least **2 correct responses for each direction** (L, R, U). Since each direction appears 3 times, the player can afford one mistake per direction.

After each input the micro:bit shows:
- **✔** — correct movement
- **✘** — wrong movement

When all 9 steps are complete, the micro:bit shows **DONE**.

---

## 5. Full Game Flow

```
Micro:bit shows "AI" on startup
        ↓
Player presses A+B
        ↓
Micro:bit shows TEST → sequence arrows (L R U L U R) → GO
        ↓
Player executes the sequence (tilt/shake)
        ↓
┌─────────────────────────────────────────┐
│ Did the player execute correctly?       │
│                                         │
│  No  → micro:bit shows ✘ then INPUT    │
│         ↳ App: "You did not execute    │
│           the sequence correctly"      │
│                                         │
│  Yes (first run, model not trained yet) │
│       → micro:bit shows DATA           │
│         ↳ App: WARNING — AI DATA       │
│           CORRUPTED → recalibrate      │
└─────────────────────────────────────────┘
        ↓
Player presses B to start training
        ↓
Micro:bit shows TRAIN → arrows one by one (L R U L R U L R U)
Player responds to each arrow immediately
Micro:bit shows ✔ or ✘ after each input
        ↓
When all 9 steps done → micro:bit shows DONE
        ↓
Player presses "Done" in the app → uploading animation → second run
        ↓
Player presses A+B again
        ↓
Same sequence: TEST → L R U L U R → GO
        ↓
Player executes correctly AND model is good (≥2 correct per direction)
        ↓
Micro:bit shows PROMPT (letter by letter: P → R → O → M → P → T)
        ↓
Player enters PROMPT in the app → SUCCESS
```

---

## 6. What the micro:bit Can Show

| Display | Meaning | App response |
|---|---|---|
| `AI` | Startup — ready | — |
| `TEST` | Test starting | — |
| Arrow (←/→/↑) | Sequence step | Player watches |
| `GO` | Execute now | Player performs movements |
| `INPUT` | Player executed wrongly | Error: "did not execute correctly" |
| `DATA` | Correct but model not trained | App shows WARNING, go to recalibrate |
| `TRAIN` | Training starting | — |
| Arrow (←/→/↑) | Training step | Player responds immediately |
| ✔ | Correct training input | — |
| ✘ | Wrong training input | — |
| `DONE` | Training complete | Player taps Done in app |
| `PROMPT` | Correct + model trained | Player enters PROMPT in app → SUCCESS |

---

## 7. How PROMPT is Revealed

The micro:bit does not show `PROMPT` all at once — it scrolls the letters one by one. Players should watch carefully and note down each letter as it appears.

The letters shown are: **P → R → O → M → P → T**

---

## 8. Troubleshooting

**Micro:bit shows INPUT even though the player thinks they did it right**
The sequence is strict: L R U L U R, in that exact order. Any missed, extra, or wrong movement will register as INPUT. Have the player try again from A+B.

**Micro:bit keeps showing DATA after training**
The model requires at least 2 correct responses for each of L, R, and U. If the player scored 0 or 1 on any direction, train again. Press B to restart training — scores reset each time.

**Micro:bit shows nothing / seems frozen**
Press the reset button on the back of the micro:bit. It will restart and show `AI`.

**Player accidentally starts training during the test (or vice versa)**
Pressing A+B during training or B during a test is blocked — the micro:bit ignores inputs from the wrong button while in an active mode. If something seems wrong, reset and start over.

**PROMPT letters scroll too fast**
The letters pause briefly between each character. If a player misses one, they must run the test again (A+B) to see it repeated — the sequence is always the same.
