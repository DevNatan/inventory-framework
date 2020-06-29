package me.saiintbrisson.minecraft.paginator;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PaginatedItem {

    ItemStack toItemStack(Player viewer, PaginatedViewHolder holder);

}
