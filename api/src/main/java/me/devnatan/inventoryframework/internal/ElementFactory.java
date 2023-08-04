package me.devnatan.inventoryframework.internal;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.logging.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Element creation factory for the current platform.
 */
public abstract class ElementFactory {

    public abstract Logger getLogger();

    /**
     * Creates a new root view for the current platform.
     *
     * @return An uninitialized configured view for the current platform.
     */
    @NotNull
    public abstract RootView createUninitializedRoot();

    /**
     * Create a new current platform container for the specified context.
     *
     * @param context The context.
     * @return A new ViewContainer.
     */
    @NotNull
    public abstract ViewContainer createContainer(@NotNull IFContext context);

    @NotNull
    public abstract Viewer createViewer(Object... parameters);

    @NotNull
    public abstract String convertViewer(Object input);

    @NotNull
    public abstract <T extends IFContext> T createContext(
            @NotNull RootView root,
            ViewContainer container,
            @NotNull List<Viewer> viewers,
            @NotNull Class<T> kind,
            @Nullable IFContext parent,
            Object initialData
	);

    @NotNull
    public abstract <T extends IFSlotContext> T createSlotContext(
            int slot,
            Component component,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull IFContext parent,
            @NotNull Class<?> kind);

    /**
     * Creates a new platform builder instance.
     *
     * @return A new platform builder instance.
     */
    public abstract ComponentBuilder<?> createComponentBuilder(@NotNull VirtualView root);

    public abstract boolean worksInCurrentPlatform();

    public abstract Job scheduleJobInterval(@NotNull RootView root, long intervalInTicks, @NotNull Runnable execution);
}
