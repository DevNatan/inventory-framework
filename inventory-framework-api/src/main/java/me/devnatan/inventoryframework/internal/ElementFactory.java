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
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.logging.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Element creation factory for the current platform.
 * <p>
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public abstract class ElementFactory {

    /**
     * Create a new current platform container for the specified context.
     *
     * @param context The context.
     * @return A new ViewContainer.
     */
    public abstract ViewContainer createContainer(IFContext context);

    public abstract Viewer createViewer(Object entity, IFRenderContext context);

    public abstract IFOpenContext createOpenContext(
            RootView root, Viewer subject, List<Viewer> viewers, Object initialData);

    public abstract IFRenderContext createRenderContext(
            UUID id,
            RootView root,
            ViewConfig config,
            ViewContainer container,
            Map<String, Viewer> viewers,
            Viewer subject,
            Object initialData);

    public abstract IFSlotClickContext createSlotClickContext(
            int slotClicked,
            Viewer whoClicked,
            ViewContainer interactionContainer,
            Component componentClicked,
            Object origin,
            boolean combined);

    /**
     * Creates a new close context for the current platform.
     *
     * @param viewer The viewer that is currently the subject of the event of close.
     * @return A new close context instance.
     */
    public abstract IFCloseContext createCloseContext(Viewer viewer, IFRenderContext parent);

    /**
     * Creates a new platform builder instance.
     *
     * @return A new platform builder instance.
     */
    public abstract ItemComponentBuilder createItemComponentBuilder(VirtualView root);

    public abstract boolean worksInCurrentPlatform();

    public abstract Job scheduleJobInterval(RootView root, long intervalInTicks, Runnable task);
}
