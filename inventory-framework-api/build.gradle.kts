plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.kotlin)
}

inventoryFramework {
    publish = true
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(libs.adventure.api)
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}