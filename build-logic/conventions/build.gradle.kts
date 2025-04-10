plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish.base") version "0.31.0" apply false
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.31.0")
}

gradlePlugin {
    plugins {
        create("libraryConvention") {
            id = "me.devnatan.inventoryframework.library"
            implementationClass = "LibraryConventionPlugin"
        }
    }
}
