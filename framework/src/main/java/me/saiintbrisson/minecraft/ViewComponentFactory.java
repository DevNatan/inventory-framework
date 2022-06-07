package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public interface ViewComponentFactory {

	@NotNull
	AbstractView createView(
		int rows,
		String title,
		@NotNull ViewType type
	);

	void setupView(@NotNull AbstractView view);

	@NotNull
	ViewContainer createContainer(
		@NotNull final VirtualView view,
		final int size,
		final String title,
		final ViewType type
	);

	@NotNull
	Viewer createViewer(Object... parameters);

	@NotNull
	BaseViewContext createContext(
		@NotNull final AbstractView root,
		final ViewContainer container,
		final Class<? extends ViewContext> backingContext
	);

	boolean worksInCurrentPlatform();

}
