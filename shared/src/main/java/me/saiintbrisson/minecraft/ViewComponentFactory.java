package me.saiintbrisson.minecraft;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.VirtualView;
import me.saiintbrisson.minecraft.internal.platform.ViewContainer;
import me.saiintbrisson.minecraft.internal.platform.Viewer;
import me.saiintbrisson.minecraft.logging.Logger;
import me.saiintbrisson.minecraft.logging.NoopLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ViewComponentFactory {

    private static final Logger noopLogger = new NoopLogger();

    @Getter(AccessLevel.PROTECTED)
    private final Map<String, Consumer<AbstractView>> modifiers = new HashMap<>();

    /**
     * Registers a new view setup modifier.
     *
     * @param id       The modifier identifier.
     * @param modifier The modifier consumer instance.
     */
    public final void registerModifier(@NotNull String id, @NotNull Consumer<AbstractView> modifier) {
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
    public abstract AbstractView createView(int rows, String title, @NotNull ViewType type);

    public abstract void setupView(@NotNull AbstractView view);

    @NotNull
    public abstract ViewContainer createContainer(@NotNull VirtualView view, int size, String title, ViewType type);

    @NotNull
    public abstract Viewer createViewer(Object... parameters);

    @NotNull
    public abstract BaseViewContext createContext(
            @NotNull AbstractView root, ViewContainer container, Class<? extends IFContext> backingContext);

    @NotNull
    public abstract AbstractViewSlotContext createSlotContext(
            int slot, ViewItem item, IFContext parent, ViewContainer container, int index, Object value);

    public abstract Object createItem(@Nullable Object stack);

    public abstract boolean worksInCurrentPlatform();

    public Logger getLogger() {
        return noopLogger;
    }
}
