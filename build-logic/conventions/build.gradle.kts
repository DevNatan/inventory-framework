plugins {
    `kotlin-dsl`
    id("com.vanniktech.maven.publish.base") version "0.32.0" apply false
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.4")
    implementation("com.vanniktech.maven.publish:com.vanniktech.maven.publish.gradle.plugin:0.32.0")
}

gradlePlugin {
    plugins {
        create("libraryConvention") {
            id = "me.devnatan.inventoryframework.library"
            implementationClass = "LibraryConventionPlugin"
        }
    }
}
