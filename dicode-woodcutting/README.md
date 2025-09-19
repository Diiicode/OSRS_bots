# woodcuttingbot (Lv 30 → 70, Draynor Willows)

A **RuneMate** bot focused on **willow** trees near **Draynor Village**. It starts by the shoreline willows, chops logs, **banks at Draynor**, then returns to the **same spot** and repeats. Ideal progression after `woodcuttingbotstart` (1→30).

> ⚠️ **Educational use only.** Botting violates Jagex ToS and can lead to bans. Use at your own risk on disposable accounts.

---

## ✨ Features

* **Target**: Draynor shoreline **Willows** (fast XP 30→70)
* **Banking**: Deposits at **Draynor Bank**, keeps axe
* **Return‑to‑origin**: Remembers the **start tile** and navigates back after banking
* **Tree selection**: Chooses reachable, non‑occupied willows; handles respawns
* **Anti‑patterning**: randomized waits, camera nudges, occasional alternate tree
* **Interrupt handling**: level‑up dialogs, small path detours

---

## 📦 Requirements

* **Java 17**, **IntelliJ IDEA**, **RuneMate Client/SDK** (developer mode)
* This bot lives under:

```
OSRS_bots/
└─ dicode-woodcutting/
   └─ src/main/java/com/runemate/woodcutting/
      └─ WoodcuttingBot.java
```

> For early levels (1→30), use **`woodcuttingbotstart`**.

---

## 🚀 Quick Start

```bash
cd dicode-woodcutting
./gradlew clean build   # Windows: gradlew.bat
```

1. Launch RuneMate (dev mode) from IntelliJ.
2. Stand **south‑west of Draynor Bank**, at the **coastline willows** (near the nets/bridge).
3. Select **woodcuttingbot** → **Start**. The bot stores your **start tile** and begins.

Ensure you have a usable **axe** (equipped or in inventory).

---

## ⚙️ Configuration

Minimal, designed to be “start and go”. Tweak constants in `WoodcuttingBot.java` if desired:

```java
private static final String TREE_NAME = "Willow";
private static final Mode MODE = Mode.BANK; // this bot always banks
// Draynor willows area + Draynor bank area are defined in code
// HOME tile captured from player's position at start
```

Future UI options (WIP): toggle camera nudges, adjust tree radius, bank strategy.

---

## 🧠 Behavior

```
OnStart → CaptureHomeTile → EnsureAxe → NavigateTo(WillowArea)
→ ChopTree → [Inventory Full?]
     ├─ yes → NavigateTo(DraynorBank) → DepositAllLogs(keep axe)
     │        → NavigateTo(HomeTile) → NextTree
     └─ no  → AntiPattern(brief idle/camera nudge) → NextTree
```

**Key checks (typical RuneMate API):**

* Query by name/ID for willows; pick nearest reachable not currently chopped
* Wait for animation / inventory delta to confirm cut
* Banking via `Bank.open()`, `depositAllExcept(axe)`
* Re‑path to **HomeTile** using web‑walk or local pathfinder
* If **level ≥ 70** (detected via `Skill.WOODCUTTING.getCurrentLevel()`), optionally **notify** to switch training method

---

## 🧪 Testing Tips

* Start exactly in the willow cluster **just south‑west of the Draynor bank**; confirm the bot saves the **home tile** once.
* Simulate full inventory to test the banking loop and return path.
* Watch logs/console; tune waits and selection (e.g., prefer trees not behind other players).

---

## 🧩 Manifest

```java
@Manifest(
    name = "Dicode Woodcutting – Willows (30→70)",
    description = "Cuts willows near Draynor, banks, and returns to the same spot.",
    version = "0.1.0",
    author = "Diiicode"
)
```

---

## 🔁 Roadmap

* [ ] Unified runner: auto‑handoff from **start (1→30)** → **willows (30→70)**
* [ ] Overlay (XP/hr, logs/hr, runtime, level)
* [ ] Robust stuck detection & web‑walk fallback routes
* [ ] Optional power‑drop mode for alternative training spots

---

## ❓ Troubleshooting

* **Bot not listed**: check `@Manifest` and package path under `src/main/java`.
* **Not chopping**: wrong tree name/IDs or obstructed line; rotate camera.
* **Doesn’t return to same spot**: validate home tile captured on start; bank route reachable.
* **Build problems**: Gradle JVM must be **JDK 17** in IntelliJ settings.

---

## 👤 Author

**Diogo Serra (Diiicode)** — learning Java automation with the RuneMate SDK.

---

### Legal & Ethics

This project is for **learning**. Automation in OSRS may result in bans and harms the community. Please experiment responsibly.

