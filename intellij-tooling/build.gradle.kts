import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
    kotlin("jvm") version "1.7.10"
}

group = "me.devnatan.if.tooling"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
}

intellij {
    version.set("IC-2022.2.3")
    plugins.set(listOf("java"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

tasks {
    buildSearchableOptions {
        enabled = false
    }

    test {
        useJUnitPlatform()
    }

    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }

    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}