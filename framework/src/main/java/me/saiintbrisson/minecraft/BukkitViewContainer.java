package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class BukkitViewContainer implements ViewContainer {

	@NotNull
	abstract Inventory getInventory();

	@Override
	public void open(@NotNull Iterable<Viewer> viewers) {
		viewers.forEach(viewer -> viewer.open(this));
	}

	@Override
	public void close() {
		getInventory().getViewers().forEach(HumanEntity::closeInventory);
	}

}