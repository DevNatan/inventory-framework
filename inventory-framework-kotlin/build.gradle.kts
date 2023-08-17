apply(from = "../library.gradle")
apply(from = "../publish.gradle")

plugins {
    kotlin("jvm") version "1.9.0"
}

dependencies {
    implementation(projects.inventoryFrameworkApi)
}