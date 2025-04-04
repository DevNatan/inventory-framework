plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
}

gradlePlugin {
    plugins {
        create("libraryConvention") {
            id = "me.devnatan.inventoryframework.library"
            implementationClass = "LibraryConventionPlugin"
        }
    }
}
