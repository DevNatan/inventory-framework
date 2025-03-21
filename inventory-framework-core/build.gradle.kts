plugins {
    id("me.devnatan.inventoryframework.library")
}

inventoryFramework {
    publish = true
}

dependencies {
    api(projects.inventoryFrameworkApi)
    testImplementation(projects.inventoryFrameworkApi)
    testImplementation(projects.inventoryFrameworkTest)
}