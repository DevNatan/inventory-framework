package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.mockito.Mockito.mock;

public class MockComponentFactory extends ViewComponentFactory {

	@Override
	public @NotNull AbstractView createView(int rows, String title, @NotNull ViewType type) {
		return mock(AbstractView.class);
	}

	@Override
	public void setupView(@NotNull AbstractView view) {

	}

	@Override
	public @NotNull ViewContainer createContainer(@NotNull VirtualView view, int size, String title, ViewType type) {
		return mock(ViewContainer.class);
	}

	@Override
	public @NotNull Viewer createViewer(Object... parameters) {
		return mock(Viewer.class);
	}

	@Override
	public @NotNull BaseViewContext createContext(@NotNull AbstractView root, ViewContainer container, Class<? extends ViewContext> backingContext) {
		return mock(BaseViewContext.class);
	}

	@Override
	public @NotNull AbstractViewSlotContext createSlotContext(ViewItem item, ViewContext parent, int paginatedItemIndex, Object paginatedItemValue) {
		return mock(AbstractViewSlotContext.class);
	}

	@Override
	public Object createItem(@Nullable Object stack) {
		return null;
	}

	@Override
	public boolean worksInCurrentPlatform() {
		return true;
	}
}
