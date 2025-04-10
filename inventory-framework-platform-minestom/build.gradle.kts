plugins {
    id("me.devnatan.inventoryframework.library")
    kotlin("jvm")
    alias(libs.plugins.shadowjar)
}

dependencies {
    api(projects.inventoryFrameworkPlatform)
    compileOnly(libs.minestom)
    testCompileOnly(libs.minestom)
    testImplementation(projects.inventoryFrameworkApi)
    testImplementation(projects.inventoryFrameworkTest)
}

tasks.shadowJar {
    archiveBaseName.set("inventory-framework")
    archiveAppendix.set("minestom")

    dependencies {
        include(project(":inventory-framework-platform"))
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}