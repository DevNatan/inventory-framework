package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

final class BukkitViewContainerFactory implements ViewContainerFactory {

	@NotNull
	@Override
	public ViewContainer create(
		@NotNull View view,
		int size,
		String title
	) {
		return new BukkitChestViewContainer(Bukkit.createInventory(view, size, title));
	}

}
