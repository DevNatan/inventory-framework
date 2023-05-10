package me.devnatan.inventoryframework.internal;

import static java.util.Objects.requireNonNull;
import static me.devnatan.inventoryframework.runtime.util.InventoryUtils.toInventoryType;

import me.devnatan.inventoryframework.ViewType;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

class PaperInventoryFactory extends InventoryFactory {

    @Override
    public Inventory createInventory(InventoryHolder holder, ViewType type, int size, Object title) {
        return title instanceof Component
                ? createInventoryWithComponentTitle(holder, type, size, (Component) title)
                : createInventoryWithTextTitle(holder, type, size, (String) title);
    }

    private Inventory createInventoryWithComponentTitle(
            InventoryHolder holder, ViewType type, int size, Component title) {
        final Inventory inventory;
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
