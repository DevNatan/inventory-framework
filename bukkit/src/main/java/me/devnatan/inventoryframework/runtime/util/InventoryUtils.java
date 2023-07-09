package me.devnatan.inventoryframework.runtime.util;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import me.devnatan.inventoryframework.ViewType;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public final class InventoryUtils {

    private static final Map<ViewType, InventoryType> typeMappings;

    static {
        final ImmutableMap.Builder<ViewType, InventoryType> builder = ImmutableMap.builder();
        registerInventoryType(builder, "BLAST_FURNACE", ViewType.BLAST_FURNACE);
        registerInventoryType(builder, "SHULKER_BOX", ViewType.SHULKER_BOX);
        registerInventoryType(builder, "SMOKER", ViewType.SMOKER);

        typeMappings = builder.put(ViewType.CHEST, InventoryType.CHEST)
                .put(ViewType.HOPPER, InventoryType.HOPPER)
                .put(ViewType.DROPPER, InventoryType.DROPPER)
                .put(ViewType.DISPENSER, InventoryType.DISPENSER)
                .put(ViewType.FURNACE, InventoryType.FURNACE)
                .put(ViewType.CRAFTING_TABLE, InventoryType.DISPENSER)
                .put(ViewType.BREWING_STAND, InventoryType.BREWING)
                .put(ViewType.BEACON, InventoryType.BEACON)
                .put(ViewType.ANVIL, InventoryType.ANVIL)
                .put(ViewType.VILLAGER_TRADING, InventoryType.MERCHANT)
                .build();
    }

    private InventoryUtils() {}

    private static void registerInventoryType(
            @NotNull ImmutableMap.Builder<ViewType, InventoryType> builder,
            @NotNull String name,
            @NotNull ViewType target) {
        try {
            builder.put(target, InventoryType.valueOf(name));
        } catch (final IllegalArgumentException ignored) {
        }
    }

    public static InventoryType toInventoryType(@NotNull ViewType type) {
        return typeMappings.get(type);
    }

    public static void checkInventoryTypeSupport(@NotNull ViewType type) {
        if (toInventoryType(type) != null) return;

        throw new IllegalArgumentException(
                String.format("%s view type is not supported on Bukkit platform.", type.getIdentifier()));
    }
}
