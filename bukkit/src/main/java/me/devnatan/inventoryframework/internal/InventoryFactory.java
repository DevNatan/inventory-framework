package me.devnatan.inventoryframework.internal;

import static java.util.Objects.requireNonNull;
import static me.devnatan.inventoryframework.runtime.util.InventoryUtils.toInventoryType;

import me.devnatan.inventoryframework.ViewType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

class InventoryFactory {

    public Inventory createInventory(InventoryHolder holder, ViewType type, int size, Object title) {
        return createInventoryWithTextTitle(holder, type, size, (String) title);
    }

    @SuppressWarnings("deprecation")
    protected final Inventory createInventoryWithTextTitle(
            InventoryHolder holder, ViewType type, int size, String title) {
        final Inventory inventory;
        title = title.isEmpty() ? null : title;

        if (title == null) {
            inventory = !type.isExtendable() || size == 0
                    ? Bukkit.createInventory(holder, requireNonNull(toInventoryType(type)))
                    : Bukkit.createInventory(holder, size);
        } else if (!type.isExtendable()) {
            inventory = Bukkit.createInventory(holder, requireNonNull(toInventoryType(type)), title);
        } else {
            inventory = size == 0
                    ? Bukkit.createInventory(holder, requireNonNull(toInventoryType(type)), title)
                    : Bukkit.createInventory(holder, size, title);
        }
        return inventory;
    }
}
