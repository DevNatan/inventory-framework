import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.shadowjar)
    alias(libs.plugins.bukkit)
}

dependencies {
    api(projects.inventoryFrameworkPlatform)
    runtimeOnly(projects.inventoryFrameworkAnvilInput)
    compileOnly(libs.spigot)
}

tasks.withType<ShadowJar> {
    archiveAppendix.set("bukkit")
}

bukkit {
    main = "me.devnatan.inventoryframework.runtime.InventoryFramework"
    name = "InventoryFramework"
    description = "Minecraft Inventory API framework"
    website = "https://github.com/DevNatan/inventory-framework"
    authors = listOf("SaiintBrisson", "DevNatan", "sasuked")
}
