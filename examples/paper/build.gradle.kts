
plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.bukkit)
    alias(libs.plugins.runPaper)
}

dependencies {
    implementation(projects.inventoryFrameworkPlatformBukkit)
    implementation(projects.inventoryFrameworkAnvilInput)
    compileOnly(libs.spigot)
}

tasks.shadowJar {
    archiveBaseName.set("inventory-framework-example")
    archiveAppendix.set("paper")

    dependencies {
        exclude {
            it.moduleGroup == "org.jetbrains.kotlin"
        }
    }
}

runPaper.folia.registerTask()

tasks.runServer {
    jvmArgs("-Dme.devnatan.inventoryframework.debug=true")
    minecraftVersion("1.21.10")
}

bukkit {
    main = "me.devnatan.inventoryframework.runtime.SamplePlugin"
    name = "InventoryFrameworkExample"
    version = project.version.toString()
    description = "Minecraft Inventory API framework sample plugin"
    website = "https://github.com/DevNatan/inventory-framework"
    apiVersion = "1.13"
    authors = listOf("SaiintBrisson", "DevNatan", "sasuked", "nicolube")
    foliaSupported = true

    commands {
        register("ifexample") {
            description = "This is a test command!"
            usage = "Opens example views"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}