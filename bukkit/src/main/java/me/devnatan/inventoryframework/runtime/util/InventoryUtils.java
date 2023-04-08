package me.devnatan.inventoryframework.runtime.util;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.ViewType;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InventoryUtils {

    private static final Map<ViewType, InventoryType> typeMappings = ImmutableMap.<ViewType, InventoryType>builder()
            .put(ViewType.CHEST, InventoryType.CHEST)
            .put(ViewType.HOPPER, InventoryType.HOPPER)
            .put(ViewType.DROPPER, InventoryType.DROPPER)
            .put(ViewType.DISPENSER, InventoryType.DISPENSER)
            .put(ViewType.FURNACE, InventoryType.FURNACE)
            .put(ViewType.BLAST_FURNACE, InventoryType.BLAST_FURNACE)
            .put(ViewType.CRAFTING_TABLE, InventoryType.DISPENSER)
            .put(ViewType.BREWING_STAND, InventoryType.BREWING)
            .put(ViewType.BEACON, InventoryType.BEACON)
            .put(ViewType.ANVIL, InventoryType.ANVIL)
            .put(ViewType.SHULKER_BOX, InventoryType.SHULKER_BOX)
            .put(ViewType.SMOKER, InventoryType.SMOKER)
            .put(ViewType.VILLAGER_TRADING, InventoryType.MERCHANT)
            .build();

    public static InventoryType toInventoryType(@NotNull ViewType type) {
        return typeMappings.get(type);
    }

    public static void checkInventoryTypeSupport(@NotNull ViewType type) {
        if (toInventoryType(type) != null) return;

        throw new IllegalArgumentException(
                String.format("%s view type is not supported on Bukkit platform.", type.getIdentifier()));
    }
}
