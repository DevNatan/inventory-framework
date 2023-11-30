package me.devnatan.inventoryframework.internal;

import me.devnatan.inventoryframework.IFDebug;
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
        String paperINFQName = "me.devnatan.inventoryframework.internal.PaperInventoryFactory";
        InventoryFactory factory = new BukkitInventoryFactory();
        try {
            final Class<?> clazz = Class.forName(paperINFQName);
            factory = (InventoryFactory) clazz.newInstance();
        } catch (final ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
        }

        try {
            Class.forName("com.destroystokyo.paper.ParticleBuilder");
        } catch (final ClassNotFoundException ignored) {
            if (factory.getClass().getName().equals(paperINFQName))
                throw new RuntimeException("inventory-framework-paper is loaded but current platform is not Paper.");
        }

        IFDebug.debug("Using %s", factory.getClass().getName());
        return factory;
    }

    public abstract Inventory createInventory(InventoryHolder holder, ViewType type, int size, Object title);
}
