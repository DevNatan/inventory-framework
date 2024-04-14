import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadowjar)
}

dependencies {
    compileOnly(libs.paperSpigot)
    implementation(projects.inventoryFrameworkPlatformBukkit)
}

java {
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<ShadowJar> {
    dependencies {
        exclude { dep -> dep.moduleGroup == "org.jetbrains.kotlin" }
    }
}