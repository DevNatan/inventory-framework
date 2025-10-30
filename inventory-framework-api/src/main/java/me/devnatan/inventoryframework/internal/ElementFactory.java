package me.devnatan.inventoryframework.internal;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.logging.Logger;
import me.devnatan.inventoryframework.state.timer.TimerState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public abstract Viewer createViewer(@NotNull Object entity, IFRenderContext context);

    public abstract IFOpenContext createOpenContext(
            @NotNull RootView root, @Nullable Viewer subject, @NotNull List<Viewer> viewers, Object initialData);

    public abstract IFRenderContext createRenderContext(
            @NotNull UUID id,
            @NotNull RootView root,
            @NotNull ViewConfig config,
            ViewContainer container,
            @NotNull Map<String, Viewer> viewers,
            Viewer subject,
            Object initialData);

    public abstract IFSlotClickContext createSlotClickContext(
            int slotClicked,
            @NotNull Viewer whoClicked,
            @NotNull ViewContainer interactionContainer,
            @Nullable Component componentClicked,
            @NotNull Object origin,
            boolean combined);

    public abstract IFSlotRenderContext createSlotRenderContext(
            int slot, @NotNull IFRenderContext parent, @Nullable Viewer viewer);

    /**
     * Creates a new close context for the current platform.
     *
     * @param viewer The viewer that is currently the subject of the event of close.
     * @return A new close context instance.
     */
    public abstract IFCloseContext createCloseContext(
            @NotNull Viewer viewer, @NotNull IFRenderContext parent, @NotNull Object origin);

    /**
     * Creates a new platform builder instance.
     *
     * @return A new platform builder instance.
     */
    public abstract ComponentBuilder<?, ?> createComponentBuilder(@NotNull VirtualView root);

    public abstract boolean worksInCurrentPlatform();

    public abstract Job scheduleJobInterval(@NotNull RootView root, long intervalInTicks, @NotNull Runnable execution);

    public abstract TimerState createTimerState(long stateId, long intervalInTicks);
}
