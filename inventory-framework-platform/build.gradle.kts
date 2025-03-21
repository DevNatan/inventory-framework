plugins {
    id("me.devnatan.inventoryframework.library")
}

inventoryFramework {
    publish = true
}

dependencies {
    api(projects.inventoryFrameworkCore)
    testImplementation(projects.inventoryFrameworkApi)
    testImplementation(projects.inventoryFrameworkCore)
    testImplementation(projects.inventoryFrameworkTest)
}