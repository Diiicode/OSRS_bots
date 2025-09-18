# OSRS\_bots

A collection of **RuneMate** bots for Old School RuneScape (OSRS), written in **Java**. This repo exists to learn **Java**, **Gradle**, **IntelliJ IDEA**, and the **RuneMate SDK**.

> ⚠️ **Educational purposes only.** Botting violates Jagex’s Terms of Service and may lead to permanent bans. You are solely responsible for how you use this code. Do not use on accounts you value.

---

## 🚀 Quick Start

### 1) Install the tools

* **RuneMate Client** (and create a dev account)
* **RuneMate Docs** (reference while developing)
* **Java JDK 17 (LTS)**
* **IntelliJ IDEA** (Community is fine)

> Ensure IntelliJ is using JDK **17** for the Gradle JVM (File → Settings → Build Tools → Gradle).

### 2) Get the project

```bash
git clone https://github.com/Diiicode/OSRS_bots.git
cd OSRS_bots
```

### 3) Open in IntelliJ

Open the folder. IntelliJ will detect `Gradle` (files: `build.gradle.kts`, `settings.gradle.kts`) and **sync dependencies** automatically.

### 4) Build

From IntelliJ’s **Gradle** tool window or the terminal:

```bash
./gradlew clean build   # Windows: gradlew.bat
```

### 5) Run with RuneMate

Follow the official docs to launch the **RuneMate client in developer mode** from IntelliJ, log in to RuneScape, select your bot, and click **Start**.

> Use a throwaway account; respect the game rules.

---

## 📂 Project Layout

```
OSRS_bots/
├─ src/main/java/com/runemate/            # Bot source
│  └─ ExampleBot.java                     # Example skeleton bot
├─ build.gradle.kts                       # Build config
├─ settings.gradle.kts                    # Project settings
├─ gradlew / gradlew.bat                  # Gradle wrappers
└─ README.md
```

---

## ⚙️ Bot Manifest

Every bot needs a **manifest annotation** so RuneMate can detect and load it.

```java
package com.runemate;

import com.runemate.game.api.script.framework.AbstractBot;
import com.runemate.game.api.script.framework.listeners.events.MessageEvent;
import com.runemate.game.api.script.framework.listeners.engine.EngineListener;
import com.runemate.game.api.hybrid.util.Resources;
import com.runemate.game.api.script.framework.AbstractBot.Description;
import com.runemate.game.api.script.framework.AbstractBot.Manifest;

@Manifest(
    name = "Woodcutting Bot",
    description = "Cuts and banks logs at specific locations.",
    version = "1.0.0",
    author = "Diiicode"
)
public class ExampleBot extends AbstractBot implements EngineListener {

    @Override
    public void onStart(String... args) {
        getLogger().info("Starting Woodcutting Bot…");
    }

    @Override
    public void onStop() {
        getLogger().info("Bot stopped.");
    }

    @Override
    public void onLoop() {
        // TODO: implement behavior tree / state machine
        // Example: chop → wait → bank → return
    }
}
```

> Namespaces and base classes vary with SDK version—use the RuneMate docs for the exact imports and lifecycle methods.

---

## 🛠️ Dev Tips

* **Gradle wrapper**: always use `./gradlew` so everyone builds with the same Gradle version.
* **Code style**: keep methods small; model your bot as a **state machine** (Idle → Navigate → Act → Bank → Return).
* **Logging**: prefer `getLogger().info/debug` over `System.out.println`.
* **Testing safely**: run short sessions; watch for stuck states; build a dry‑run mode where actions are logged but not executed.

---

## ❓ Troubleshooting

* IntelliJ can’t find JDK: set Gradle JVM to **17**.
* Dependencies missing: **Reimport Gradle** project / `./gradlew --refresh-dependencies`.
* RuneMate not detecting bot: confirm the **`@Manifest`** annotation and correct **package** under `src/main/java`.

---

## 👤 Author

**Diogo Serra** — [GitHub @Diiicode](https://github.com/Diiicode)
42 student (C, Bash, sysadmin, security). Learning automation & RuneMate SDK.

---

### Legal & Ethics

This repository is for **learning**. Using bots in OSRS can result in bans and harms the game community. Please experiment responsibly and respect the game’s Terms of Service.
