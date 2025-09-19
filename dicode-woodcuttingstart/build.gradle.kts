// Applies the RuneMate Gradle plugin which wires the client, API deps and tasks
plugins {
    id("com.runemate") version "1.5.1"
}

// Standard Maven repository used by the plugin to resolve dependencies
repositories { mavenCentral() }

// Force compilation on Java 17 which RuneMate targets
java {
    toolchain { languageVersion.set(JavaLanguageVersion.of(17)) }
}

// Configure how the RuneMate client launches and define the bot manifest it should generate
runemate {
    // Launch the client in dev mode (enables Dev Toolkit etc.)
    devMode = true
    // Allow the client to log in automatically using the account you select in Spectre
    autoLogin = true

    // Generate a JSON manifest for our bot so Spectre can find and run it
    manifests {
        create("Dicode StarterWoodcutter") {
            // Fully-qualified class name of the bot entrypoint below
            mainClass = "com.runemate.woodcutting.Woodcutter"
            // Short summary shown in the client
            tagline = "Cuts nearby trees until inventory is full"
            // Longer description for clarity while testing
            description = "Simple Startwoodcutting bot"
            // Your local dev version; bump as you iterate
            version = "0.1.0"
            // Internal identifier (must be unique on your machine/workspace)
            internalId = "dicode-woodcutter"
        }
    }
}


