package me.devnatan.inventoryframework.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MockElementFactory extends ElementFactory {
    @Override
    public Logger getLogger() {
        return mock(Logger.class);
    }

    @Override
    public @NotNull RootView createUninitializedRoot() {
        return mock(RootView.class);
    }

    @Override
    public @NotNull ViewContainer createContainer(
            @NotNull IFContext context, int size, @Nullable String title, @Nullable ViewType type) {
        return mock(ViewContainer.class);
    }

    @Override
    public @NotNull Viewer createViewer(Object... parameters) {
        return mock(Viewer.class);
    }

    @Override
    public @NotNull String transformViewerIdentifier(Object input) {
        return (String) input;
    }

    @Override
    public <T extends IFContext> @NotNull T createContext(
            @NotNull RootView root,
            ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull Class<T> kind,
            boolean shared,
            @Nullable IFContext parent) {
        return mock(kind);
    }

    @Override
    public <T extends IFSlotContext> @NotNull T createSlotContext(
            int slot,
            Component component,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull IFContext parent,
            @NotNull Class<?> kind) {
        @SuppressWarnings("unchecked")
        T value = (T) mock(kind);
        when(value.getSlot()).thenReturn(slot);
        when(value.getParent()).thenReturn(parent);
        when(value.getContainer()).thenReturn(container);
        return value;
    }

    @Override
    public boolean worksInCurrentPlatform() {
        return true;
    }
}
