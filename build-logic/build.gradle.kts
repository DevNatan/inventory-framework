plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
}
