plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(libs.adventure.api)
}

inventoryFramework {
    publish = true
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}