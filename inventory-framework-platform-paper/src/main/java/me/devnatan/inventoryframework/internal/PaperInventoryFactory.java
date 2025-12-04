package me.devnatan.inventoryframework.internal;

import static java.util.Objects.requireNonNull;

import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate;
import me.devnatan.inventoryframework.runtime.util.InventoryUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * InventoryFactory implementation for PaperSpigot software.
 * Supports {@link Component} as inventory title.
 */
@SuppressWarnings("unused")
final class PaperInventoryFactory extends BukkitInventoryFactory {

    PaperInventoryFactory() {}

    @Override
    public Inventory createInventory(InventoryHolder holder, ViewType type, int size, Object title) {
        if (!(title instanceof Component)) return super.createInventory(holder, type, size, title);

        final Inventory inventory;
        final Component titleAsComponent = (Component) title;

        if (!type.isExtendable()) {
            inventory = Bukkit.createInventory(
                    holder, requireNonNull(InventoryUtils.toInventoryType(type)), titleAsComponent);
        } else {
            inventory = size == 0
                    ? Bukkit.createInventory(
                            holder, requireNonNull(InventoryUtils.toInventoryType(type)), titleAsComponent)
                    : Bukkit.createInventory(holder, size, titleAsComponent);
        }
        return inventory;
    }

    @Override
    public void setInventoryTitle(Player player, Object newTitle) {
        if (newTitle instanceof Component) {
            InventoryUpdate.updateInventory(player, newTitle);
            return;
        }

        super.setInventoryTitle(player, newTitle);
    }
}
