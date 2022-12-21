package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.VirtualView;
import me.saiintbrisson.minecraft.internal.platform.ViewContainer;
import me.saiintbrisson.minecraft.internal.platform.Viewer;
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
            int slot, ViewItem item, IFContext parent, ViewContainer container, int index, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object createItem(@Nullable Object stack) {
        throw new UnsupportedOperationException();
    }
}
