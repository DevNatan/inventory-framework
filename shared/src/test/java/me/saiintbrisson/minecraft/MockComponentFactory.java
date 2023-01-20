package me.saiintbrisson.minecraft;

import static org.mockito.Mockito.mock;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockComponentFactory extends ViewComponentFactory {

    @Override
    public @NotNull AbstractView createView(int rows, String title, @NotNull ViewType type) {
        return mock(AbstractView.class);
    }

    @Override
    public void setupView(@NotNull AbstractView view) {}

    @Override
    public @NotNull ViewContainer createContainer(@NotNull VirtualView view, int size, String title, ViewType type) {
        return mock(ViewContainer.class);
    }

    @Override
    public @NotNull Viewer createViewer(Object... parameters) {
        return mock(Viewer.class);
    }

    @Override
    public @NotNull BaseViewContext createContext(
            @NotNull AbstractView root, ViewContainer container, Class<? extends IFContext> backingContext) {
        return mock(BaseViewContext.class);
    }

    @Override
    public @NotNull AbstractViewSlotContext createSlotContext(
            int slot, IFItem item, IFContext parent, ViewContainer container, int index, Object value) {
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
