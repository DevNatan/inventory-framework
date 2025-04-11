plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.kotlin)
}

inventoryFramework {
    publish = true
}

dependencies {
    compileOnly(libs.adventure.api)
}

kotlin {
    explicitApi()
    jvmToolchain(8)
}