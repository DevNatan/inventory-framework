package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public @NotNull BaseViewContext createContext(
            @NotNull AbstractView root, ViewContainer container, Class<? extends ViewContext> backingContext) {
        throw new UnsupportedOperationException();
    }

	@Override
	public @NotNull AbstractViewSlotContext createSlotContext(ViewItem item, ViewContext parent, int paginatedItemIndex, Object paginatedItemValue) {
		throw new UnsupportedOperationException();
	}

	@Override
    public Object createItem(@Nullable Object stack) {
        throw new UnsupportedOperationException();
    }
}
