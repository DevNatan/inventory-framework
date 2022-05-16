package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

final class BukkitOpenViewContext extends OpenViewContext {

	BukkitOpenViewContext(@NotNull AbstractView view) {
		super(view);
	}

	@Override
	public Player getPlayer() {
		final Viewer viewer = getViewers().get(0);
		if (viewer == null)
			throw new IllegalStateException("Tried to retrieve context player while it's not valid anymore.");

		return ((BukkitViewer) viewer).getPlayer();
	}

	@Override
	public void setContainerSize(int containerSize) {
		super.setContainerSize((getContainerType() == null ? AbstractView.DEFAULT_TYPE : getContainerType()).normalize(containerSize));
	}

}