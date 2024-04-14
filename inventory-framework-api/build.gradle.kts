plugins {
    alias(libs.plugins.kotlin)
}

dependencies {
    compileOnly(libs.adventure.api)
}

kotlin {
    explicitApi()
}