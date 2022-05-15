@file:Suppress("UnstableApiUsage")

plugins {
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