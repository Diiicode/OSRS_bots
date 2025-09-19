# woodcuttingbotstart (Lv 1 → 30)

A focused **RuneMate** bot for the **early game**: from **Woodcutting 1 to \~30** around **Draynor Village**. It chops nearby normal trees, walks to **Draynor Bank**, deposits, and repeats — perfect as a starter before switching to the regular bot for **30 → 70**.

> ⚠️ **Educational purposes only.** Botting violates Jagex’s Terms of Service and can lead to bans. Use on throwaway accounts; you are responsible for your usage.

---

## Why two bots?

* **`woodcuttingbotstart`**: ultra‑simple, low‑level areas (Draynor bridge/shore), optimized for normal trees and constant banking.
* **`woodcuttingbot` (regular)**: post‑30 progression (oaks → willows → …), broader areas and logic.

> **WIP**: a unified runner that auto‑switches at level **30** (`Skill.WOODCUTTING.getCurrentLevel()`) from *start* → *regular*.

---

## ✨ What it does

* Spawns **near Draynor** (by the **bridge** and **coastline trees**) and starts immediately.
* Finds the nearest **Normal Tree** with a clear line of action.
* Interacts **“Chop down”**, waits for animation/log change.
* On **full inventory** → runs to **Draynor Bank**, deposits **all logs** (keeps axe), returns to trees.
* Light **anti‑patterning** (short randomized waits, camera nudges, alternate tree selection).
* Handles common interruptions (level‑up dialogs, small path detours).

---

## 📦 Requirements

* **Java 17**, **IntelliJ IDEA**, **RuneMate Client/SDK** (developer mode)
* This subproject lives at:

```
OSRS_bots/
└─ dicode-woodcutting/
   └─ src/main/java/com/runemate/woodcutting/
      └─ WoodcuttingBotStart.java
```

---

## 🏃 Quick start

```bash
# from repo root
cd dicode-woodcutting
./gradlew clean build     # Windows: gradlew.bat
```

1. Launch **RuneMate** in **developer mode** from IntelliJ.
2. Log into OSRS and stand near **Draynor bridge / shore trees**.
3. Choose **woodcuttingbotstart** → **Start**.

> Ensure you have an axe you can use (equipped or in inventory).

---

## ⚙️ Configuration (minimal)

This bot is meant to be “hit start and go”. If you want to tweak it, edit the constants in `WoodcuttingBotStart.java`:

```java
private static final String TREE_NAME = "Tree";      // normal trees
private static final Mode MODE = Mode.BANK;          // always bank
// prebuilt areas for Draynor shore/bridge & bank are defined in-code
```

**Planned**: optional UI to set tree area radius and toggle camera nudges.

---

## 🧠 Behavior overview

```
Start → ValidateAxe → NavigateTo(DraynorTrees) →
  ChopTree → [Inventory Full?]
      ├─ yes → NavigateTo(DraynorBank) → DepositLogs(keep axe) → ReturnToTrees
      └─ no  → AntiPattern(brief idle/camera nudge) → NextTree
```

Key checks (typical RuneMate API calls):

* `Skill.WOODCUTTING.getCurrentLevel()` → if ≥ 30: **notify** user to switch (or auto‑handoff in WIP).
* `Inventory.isFull()` and `Bank.open() / depositAllExcept(axe)`.
* Tree queries by name/ID, reachable, not already being chopped.

---

## 🔁 Roadmap

* [ ] Unified runner that **auto‑switches** at Lv 30 to `woodcuttingbot` (regular)
* [ ] Simple overlay: current level, XP/hr, logs/hr, run time
* [ ] Better stuck detection & web‑walk fallback
* [ ] Config UI (area radius, drop vs bank, camera options)

---

## 🧪 Testing tips

* Start at **Draynor** facing the shore trees; verify the bot picks trees nearest the **bridge**.
* Test **bank loop**: fill inventory quickly (spawned logs) and watch deposit logic.
* Keep early sessions short; review logs; tune wait windows and selection filters.

---

## 🧩 Manifest

```java
@Manifest(
    name = "Dicode Woodcutting – Start (1→30)",
    description = "Chops normal trees near Draynor (bridge/shore) and banks at Draynor.",
    version = "0.1.0",
    author = "Diiicode"
)
```

---

## 👤 Author

**Diogo Serra (Diiicode)** — learning Java automation with RuneMate SDK.

---

### Legal & Ethics

For **learning** only. Automation breaches Jagex ToS and can result in bans. Respect the game and its community.

