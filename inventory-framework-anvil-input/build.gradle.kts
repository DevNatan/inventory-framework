plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.kotlin)
}

inventoryFramework {
    publish = true
}

dependencies {
    compileOnly(libs.spigot)
    compileOnlyApi(projects.inventoryFrameworkPlatformBukkit)
}