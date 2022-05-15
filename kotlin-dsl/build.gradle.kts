plugins {
    alias(libs.plugins.kotlin)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":framework"))
    compileOnly(libs.kotlin)
}

kotlin {
    explicitApi()
}