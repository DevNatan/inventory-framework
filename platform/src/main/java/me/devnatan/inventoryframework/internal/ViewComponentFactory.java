package me.devnatan.inventoryframework.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.devnatan.inventoryframework.logging.Logger;
import me.saiintbrisson.minecraft.logging.NoopLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ViewComponentFactory {

    private static final Logger noopLogger = new NoopLogger();

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Consumer<RootView>> modifiers = new HashMap<>();

    /**
     * Registers a new view setup modifier.
     *
     * @param id       The modifier identifier.
     * @param modifier The modifier consumer instance.
     */
    public final void registerModifier(@NotNull String id, @NotNull Consumer<RootView> modifier) {
        synchronized (modifiers) {
            modifiers.put(id, modifier);
        }
    }

    /**
     * Unregisters a view setup modifier.
     *
     * @param id The modifier identifier.
     */
    public final void unregisterModifier(@NotNull String id) {
        synchronized (modifiers) {
            modifiers.remove(id);
        }
    }

    @NotNull
    public abstract RootView createView(int rows, String title, @NotNull ViewType type);

    public abstract void setupView(@NotNull RootView view);

    @NotNull
    public abstract ViewContainer createContainer(@NotNull VirtualView view, int size, String title, ViewType type);

    @NotNull
    public abstract Viewer createViewer(Object... parameters);

    @NotNull
    public abstract IFContext createContext(
            @NotNull RootView root,
            @Nullable ViewContainer container,
            @Nullable Class<? extends IFContext> backingContext,
            @Nullable Viewer viewer);

    @NotNull
    public abstract IFSlotContext createSlotContext(
            int slot, IFItem item, IFContext parent, ViewContainer container, int index, Object value);

    public abstract Object createItem(@Nullable Object stack);

    public abstract boolean worksInCurrentPlatform();

    public Logger getLogger() {
        return noopLogger;
    }
}
