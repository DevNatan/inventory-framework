package me.devnatan.inventoryframework.example.warpList;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public interface Warp {
    UUID getId();

    String getName();

    ItemStack getIcon();
}
