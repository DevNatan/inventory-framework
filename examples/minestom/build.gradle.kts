plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.kotlin)
    id("application")
}

dependencies {
    implementation(projects.inventoryFrameworkPlatformMinestom)
    implementation(libs.minestom)
}

tasks.shadowJar {
    archiveBaseName.set("inventory-framework-example")
    archiveAppendix.set("minestom")
}

java {
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    applicationDefaultJvmArgs += "-Dme.devnatan.inventoryframework.debug=true"
    mainClass = "me.devnatan.inventoryframework.runtime.SampleServerKt"
}