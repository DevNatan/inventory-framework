package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

final class BukkitViewContainerFactory implements ViewContainerFactory {

	@Override
	public @NotNull ViewContainer createContainer(@NotNull final View view, final int size, final String title) {
		return new BukkitChestViewContainer(Bukkit.createInventory(view, size, title));
	}

}