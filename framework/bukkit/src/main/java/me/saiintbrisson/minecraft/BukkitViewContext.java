package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BukkitViewContext extends BaseViewContext {

	@NotNull
	private final Player player;

	public BukkitViewContext(
		@NotNull final View view,
		@Nullable final ViewContainer container,
		@NotNull final Player player
	) {
		super(view, container);
		this.player = player;
	}

	@Override
	@NotNull
	public Player getPlayer() {
		return player;
	}

}