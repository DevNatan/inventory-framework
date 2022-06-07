@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.ktlint)
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":framework"))
    runtimeOnly(project(":framework"))
    implementation(libs.kotlin)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
}

kotlin {
    explicitApi()
}