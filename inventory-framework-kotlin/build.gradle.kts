plugins {
    kotlin("jvm") version "1.9.0"
}

apply(from = "../library.gradle")
apply(from = "../publish.gradle")

dependencies {
    implementation(projects.inventoryFrameworkPlatform)
}