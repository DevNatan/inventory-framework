plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("com.diffplug.spotless:spotless-plugin-gradle:7.0.2")
}

gradlePlugin {
    plugins {
        create("libraryConvention") {
            id = "me.devnatan.inventoryframework.library"
            implementationClass = "LibraryConventionPlugin"
        }
    }
}
