plugins {
    id("me.devnatan.inventoryframework.library")
}

dependencies {
    compileOnly(projects.inventoryFrameworkApi)
    compileOnly(projects.inventoryFrameworkCore)
    compileOnly(libs.mockito.core)
}