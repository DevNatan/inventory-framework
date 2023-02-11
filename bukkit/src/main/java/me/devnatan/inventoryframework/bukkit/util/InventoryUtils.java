package me.devnatan.inventoryframework.bukkit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.ViewType;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InventoryUtils {

    public static InventoryType toInventoryType(@NotNull ViewType type) {
        if (type == ViewType.HOPPER) return InventoryType.HOPPER;
        if (type == ViewType.FURNACE) return InventoryType.FURNACE;
        if (type == ViewType.CHEST) return InventoryType.CHEST;

        return null;
    }

    public static void checkInventoryTypeSupport(@NotNull ViewType type) {
        if (toInventoryType(type) != null) return;

        throw new IllegalArgumentException(
                String.format("%s view type is not supported on Bukkit platform.", type.getIdentifier()));
    }
}
