package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class BukkitOpenViewContext extends OpenViewContext {

	BukkitOpenViewContext(@NotNull AbstractView view) {
		super(view);
	}

	@Override
	public Player getPlayer() {
		return BukkitViewer.toPlayerOfContext(this);
	}

}