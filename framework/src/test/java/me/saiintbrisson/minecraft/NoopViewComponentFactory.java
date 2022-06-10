package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public abstract class NoopViewComponentFactory extends ViewComponentFactory {

	@Override
	public @NotNull AbstractView createView(int rows, String title, @NotNull ViewType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setupView(@NotNull AbstractView view) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull ViewContainer createContainer(@NotNull VirtualView view, int size, String title, ViewType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull Viewer createViewer(Object... parameters) {
		throw new UnsupportedOperationException();
	}

	@Override
	public @NotNull BaseViewContext createContext(@NotNull AbstractView root, ViewContainer container, Class<? extends ViewContext> backingContext) {
		throw new UnsupportedOperationException();
	}

}