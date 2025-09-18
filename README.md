# OSRS_bots

A collection of **RuneMate bots** for Old School RuneScape, written in **Java**.  
This repository is mainly for learning **Java, Gradle, IntelliJ IDEA**, and the **RuneMate SDK**.

> ⚠️ **Educational purposes only.**  
> Botting violates Jagex’s Terms of Service and may lead to bans. Use at your own risk.

---

## 🚀 How to Start

### 1. Install the tools
- [RuneMate Client](https://www.runemate.com/)  
- [RuneMate Docs](https://runemate.gitbook.io/runemate-documentation)  
- [Java JDK 17 (LTS)](https://adoptium.net/temurin/releases/?version=17)  
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/)  

---

### 2. Get the project

git clone https://github.com/Diiicode/OSRS_bots.git
cd OSRS_bots

3. Open in IntelliJ
Open the folder in IntelliJ.
IntelliJ will detect the Gradle files (build.gradle.kts, settings.gradle.kts).
Let it sync dependencies automatically.

4. Build the project
Use the Gradle:
build project with Gradle on Intellij
run runemate with Gradle on Intellij

6. Run your bot
Started RuneMate in developer mode on Intellij (see docs).
Log into RuneScape.
Select your bot → click Start.

📂 Project Layout
bash
Copy code
OSRS_bots/
├─ src/main/java/com/runemate/<botname>/   # Bot source code
│   └─ ExampleBot.java                     # Example skeleton bot
├─ build.gradle.kts                        # Build config
├─ settings.gradle.kts                     # Project settings
├─ gradlew / gradlew.bat                   # Gradle wrappers
└─ README.md


⚙️ Bot Manifest
Every bot needs a manifest annotation so RuneMate can load it:

@Manifest(
    name = "Woodcutting Bot",
    description = "Cuts and banks logs at specific locations.",
    version = "1.0.0",
    author = "Diiicode"
)

🧑‍💻 Author
Diogo Serra — GitHub/Diiicode
42 student (C, Bash, sysadmin, security). Learning automation & RuneMate SDK.
