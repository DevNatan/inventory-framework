package me.devnatan.inventoryframework.internal;

import me.devnatan.inventoryframework.ViewType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

abstract class InventoryFactory {

    protected static InventoryFactory instance;

    public static InventoryFactory current() {
        if (instance == null) instance = forCurrentPlatform();
        return instance;
    }

    private static InventoryFactory forCurrentPlatform() {
        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");

            final Class<?> clazz = Class.forName("me.devnatan.inventoryframework.internal.PaperInventoryFramework");
            return (InventoryFactory) clazz.newInstance();
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
        }

        return new BukkitInventoryFactory();
    }

    public abstract Inventory createInventory(InventoryHolder holder, ViewType type, int size, Object title);
}
