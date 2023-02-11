package me.devnatan.inventoryframework.internal;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.logging.Logger;
import me.devnatan.inventoryframework.pipeline.OpenInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.RenderInterceptor;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
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
     * @param size    The size of the container. Fallbacks to platform default size if {@code 0}.
     * @param title   The title of the container.
     * @param type    The type of the container.
     * @return A new ViewContainer.
     */
    @NotNull
    public abstract ViewContainer createContainer(
            @NotNull IFContext context, int size, @Nullable String title, @Nullable ViewType type);

    @NotNull
    public abstract Viewer createViewer(Object... parameters);

    @NotNull
    public abstract <T extends IFContext> T createContext(
            @NotNull RootView root,
            ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull Class<T> kind,
            boolean shared);

    @NotNull
    public abstract IFSlotContext createSlotContext(
            int slot,
            IFItem<?> internalItem,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull IFContext parent);

    public abstract Object createItem(@Nullable Object stack);

    public abstract boolean worksInCurrentPlatform();

    @ApiStatus.OverrideOnly
    @MustBeInvokedByOverriders
    void registerPlatformInterceptors(@NotNull RootView view) {
        final Pipeline<? super IFContext> pipeline = view.getPipeline();
        pipeline.intercept(StandardPipelinePhases.OPEN, new OpenInterceptor());
        pipeline.intercept(StandardPipelinePhases.RENDER, new RenderInterceptor());
    }
}
