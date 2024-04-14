buildscript {
    dependencyResolutionManagement {
        repositories {
            mavenCentral()
            maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
            maven("https://oss.sonatype.org/content/repositories/snapshots")
            maven("https://repo.papermc.io/repository/maven-public/")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

rootProject.name = "inventory-framework"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("inventory-framework-api",
        "inventory-framework-core",
        "inventory-framework-platform",
        "inventory-framework-platform-paper",
        "inventory-framework-platform-bukkit",
        "inventory-framework-anvil-input",
        "inventory-framework-proxy-inventory")
