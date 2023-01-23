package me.devnatan.inventoryframework.example.warpList;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface Warp {
	UUID getId();

	String getName();

	ItemStack getIcon();
}
