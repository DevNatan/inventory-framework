package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BukkitPaginatedViewContext<T> extends BasePaginatedViewContext<T> {

	BukkitPaginatedViewContext(@NotNull AbstractView root, @Nullable ViewContainer container) {
		super(root, container);
	}

	@Override
	public Player getPlayer() {
		final Viewer viewer = getViewers().get(0);
		if (viewer == null)
			throw new IllegalStateException("Tried to retrieve context player while it's not valid anymore.");

		return ((BukkitViewer) viewer).getPlayer();
	}

}