package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class BukkitViewContext extends BaseViewContext {

	public BukkitViewContext(
		@NotNull final View view,
		@Nullable final ViewContainer container
	) {
		super(view, container);
	}

}