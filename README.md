# OSRS_bots

A collection of **RuneMate bots** for Old School RuneScape, written in **Java**.  
This repository is mainly for learning **Java, Gradle, IntelliJ IDEA**, and the **RuneMate SDK**.

This repo is my playground for:
- Practicing **Java/OOP** and clean architecture
- Learning **automation & game scripting** on RuneMate
- Experimenting with **LoopingBot**, queries, and event handling
- Mimicking human-like behavior (breaks, camera movement, small delays)

> ⚠️ **Educational purposes only.**  
> Botting violates Jagex’s Terms of Service and can lead to bans. Use at your own risk.

---

## 🚀 Getting Started

### Requirements
- **RuneMate Client** (installer): https://www.runemate.com/  
- **RuneMate Docs (Guides):** https://runemate.gitbook.io/runemate-documentation  
- **Game API JavaDoc:** https://runemate.gitlab.io/community/runemate-game-api/runemate-game-api/  
- **Java Development Kit (JDK)** – LTS recommended (e.g., 17): https://adoptium.net/temurin/releases/?version=17

### Run locally
1. Clone:
   ```bash
   git clone https://github.com/Diiicode/OSRS_bots.git
   cd OSRS_bots
Open in your IDE (IntelliJ IDEA / Eclipse / VS Code with Java).

Build your bot module and launch RuneMate.

Start the client in developer mode (see docs), then select your local bot from the client.

LoopingBot example thread: https://www.runemate.com/community/threads/loopingbot-example.23320/

📝 To-Do
 Multi-bank support & location profiles
 Anti-ban extras (mouse paths, camera nudges, idle checks)
 Small task framework (state machine/task tree)
 Diagrams & docstrings for public utilities

 ---

 # Long_Version

---

## 🚀 Quick Start

### 1. Requirements
- **RuneMate Client**: https://www.runemate.com/  
- **RuneMate SDK Docs**: https://runemate.gitbook.io/runemate-documentation  
- **Java JDK 17 (LTS)**: https://adoptium.net/temurin/releases/?version=17  
- **IntelliJ IDEA**: https://www.jetbrains.com/idea/download/  
- **Gradle** (bundled wrapper is included in this repo)  

---

### 2. Clone the Repository

git clone https://github.com/Diiicode/OSRS_bots.git
cd OSRS_bots

3. Open in IntelliJ IDEA
Open IntelliJ IDEA → Open → select the project folder.

IntelliJ will detect the Gradle build files (build.gradle.kts, settings.gradle.kts).

Sync Gradle when prompted.

4. Build with Gradle
   
To build the project and download dependencies:
./gradlew build

The compiled .jar file will be available in:
build/libs/

5. Run Your Bot
Launch RuneMate in developer mode - using Gradle

Log into RuneScape.
Select your bot from the RuneMate interface and hit Start.

🗂 Project Structure
bash
Copy code
OSRS_bots/
├─ src/main/java/com/runemate/<botname>/   # Bot source code
│   └─ ExampleBot.java                     # Example skeleton bot
├─ build.gradle.kts                        # Build config
├─ settings.gradle.kts                     # Project settings
├─ gradlew / gradlew.bat                   # Gradle wrappers
└─ README.md


⚙️ Manifest Example
Each bot must have a manifest annotation so RuneMate can identify it.

@Manifest(
    name = "Woodcutting Bot",
    description = "Cuts and banks logs at specific locations.",
    version = "1.0.0",
    author = "Diiicode"
)

📚 Learning Focus
Practicing Java & OOP
Understanding Gradle builds
Working with IntelliJ workflow
Exploring RuneMate API

⚖️ Disclaimer
These bots are for educational purposes only.
Botting OSRS is against Jagex’s Terms of Service and may lead to bans.

Use at your own risk.

🧑‍💻 Author
Diogo Serra — https://github.com/Diiicode
42 student (C, Bash, sysadmin, security). Also tinkering with automation & networking.
