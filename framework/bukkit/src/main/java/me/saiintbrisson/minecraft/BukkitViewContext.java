package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BukkitViewContext extends BaseViewContext {

	public BukkitViewContext(
		@NotNull final AbstractView root,
		@Nullable final ViewContainer container
	) {
		super(root, container);
	}

}