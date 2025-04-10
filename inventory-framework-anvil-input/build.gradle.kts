plugins {
    alias(libs.plugins.kotlin)
    id("me.devnatan.inventoryframework.library")
}

inventoryFramework {
    publish = true
}

dependencies {
    compileOnly(libs.spigot)
    compileOnlyApi(projects.inventoryFrameworkPlatformBukkit)
}