
plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.bukkit)
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(projects.inventoryFrameworkPlatformBukkit)
    implementation(projects.inventoryFrameworkAnvilInput)
    compileOnly(libs.spigot)
}

tasks.shadowJar {
    archiveBaseName.set("inventory-framework-example")
    archiveAppendix.set("bukkit")

    dependencies {
        exclude {
            it.moduleGroup == "org.jetbrains.kotlin"
        }
    }
}

tasks.runServer {
    jvmArgs("-Dme.devnatan.inventoryframework.debug=true")
    minecraftVersion("1.21.3")
}

bukkit {
    main = "me.devnatan.inventoryframework.runtime.SamplePlugin"
    name = "InventoryFrameworkExample"
    version = project.version.toString()
    description = "Minecraft Inventory API framework sample plugin"
    website = "https://github.com/DevNatan/inventory-framework"
    apiVersion = "1.20"
    authors = listOf("SaiintBrisson", "DevNatan", "sasuked", "nicolube")
    commands {
        create("ifexample") {
            description = "This is a test command!"
            permission = "ifexample.run"
            usage = "Opens example views"
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}