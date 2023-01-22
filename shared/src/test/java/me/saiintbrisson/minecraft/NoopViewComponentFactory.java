package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
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
            @NotNull AbstractView root, ViewContainer container, Class<? extends IFContext> backingContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull AbstractViewSlotContext createSlotContext(
            int slot, IFItem item, IFContext parent, ViewContainer container, int index, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object createItem(@Nullable Object stack) {
        throw new UnsupportedOperationException();
    }
}
