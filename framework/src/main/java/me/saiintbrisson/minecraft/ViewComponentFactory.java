package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public interface ViewComponentFactory {

	@NotNull
	ViewContainer createContainer(
		@NotNull final VirtualView view,
		final int size,
		final String title
	);

	@NotNull
	Viewer createViewer(Object... parameters);

	@NotNull
	ViewContext createContext(
		@NotNull final AbstractView root,
		@NotNull final ViewContainer container
	);

	boolean worksInCurrentPlatform();

}
