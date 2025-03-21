plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.shadowjar)
}

inventoryFramework {
    publish = true
}

dependencies {
    compileOnly(libs.paperSpigot)
    implementation(projects.inventoryFrameworkPlatformBukkit)
}

java {
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.shadowJar {
    archiveBaseName.set("inventory-framework")
    archiveAppendix.set("paper")

    dependencies {
        exclude {
            it.moduleGroup == "org.jetbrains.kotlin"
        }
    }
}