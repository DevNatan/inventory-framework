package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BukkitViewContext extends BaseViewContext {

	public BukkitViewContext(
		@NotNull final AbstractView root,
		@Nullable final ViewContainer container
	) {
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