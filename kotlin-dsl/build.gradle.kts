@file:Suppress("UnstableApiUsage")
apply {
    from("../publish.gradle")
}

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
    compileOnly(libs.kotlin)
}

kotlin {
    explicitApi()
}