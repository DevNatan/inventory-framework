@file:Suppress("UnstableApiUsage")

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":framework"))
    runtimeOnly(project(":framework"))
    compileOnly(libs.kotlin)
}

kotlin {
    explicitApi()
}