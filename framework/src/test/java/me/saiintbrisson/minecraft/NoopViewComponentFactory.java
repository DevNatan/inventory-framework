package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

public abstract class NoopViewComponentFactory implements ViewComponentFactory {

	@Override
	public @NotNull AbstractView createView(int rows, String title, @NotNull ViewType type) {
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
	public @NotNull BaseViewContext createContext(@NotNull AbstractView root, @NotNull ViewContainer container) {
		throw new UnsupportedOperationException();
	}

}