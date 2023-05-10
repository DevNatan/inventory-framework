package me.devnatan.inventoryframework;

import java.util.Set;
import java.util.UUID;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public interface RootView extends VirtualView, Iterable<IFContext> {

    /**
     * The unique identifier of this view.
     *
     * @return The unique id of this view.
     */
    @NotNull
    UUID getUniqueId();

    /**
     * All contexts linked to this view.
     *
     * @return An unmodifiable set of all currently active contexts in this view.
     */
    @NotNull
    @UnmodifiableView
    Set<IFContext> getContexts();

    /**
     * Returns the context that is linked to the specified viewer in this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewer The viewer.
     * @return The context of the viewer in this context.
     * @throws IllegalArgumentException If there's no context linked to the given viewer.
     */
    @ApiStatus.Internal
    @NotNull
    IFContext getContext(@NotNull Viewer viewer);

    /**
     * Returns the context that is linked to the specified viewer in this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewerId The id of the viewer.
     * @return The context of the viewer in this context.
     * @throws IllegalArgumentException If there's no context linked to the given viewer.
     */
    @NotNull
    IFContext getContext(@NotNull String viewerId);

    /**
     * Adds a context to this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context to add.
     */
    @ApiStatus.Internal
    void addContext(@NotNull IFContext context);

    /**
     * Removes a given context from this view if that context is linked to this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context to remove.
     */
    @ApiStatus.Internal
    void removeContext(@NotNull IFContext context);

    /**
     * Renders a given context in this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param context The context to render.
     */
    @ApiStatus.Internal
    void renderContext(@NotNull IFContext context);

    /**
     * Called when the view is about to be configured, the returned object will be the view's
     * configuration.
     * <p>
     * As a reference, the data defined here was defined in the constructor in previous versions.
     *
     * @param config A mutable ViewConfigBuilder to configure this view.
     */
    @ApiStatus.OverrideOnly
    void onInit(ViewConfigBuilder config);

    /**
     * The configuration for this view.
     *
     * @return The configuration for this view.
     * @throws IllegalStateException If the configuration is not available yet (uninitialized)
     */
    @NotNull
    ViewConfig getConfig();

    /**
     * Sets the configuration for this view.
     *
     * @param config The new configuration of this view.
     * @throws IllegalStateException If the configuration was already set before.
     */
    void setConfig(@NotNull ViewConfig config);

    /**
     * The execution pipeline for this view.
     *
     * @return The pipeline for this view.
     */
    @NotNull
    Pipeline<VirtualView> getPipeline();

    /**
     * Opens this view to a viewer.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param viewer The viewer.
     */
    @ApiStatus.Internal
    void open(@NotNull Viewer viewer);

    /**
     * Closes all contexts that are currently active in this view.
     */
    void closeForEveryone();

    /**
     * The ElementFactory for this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return The current element factory.
     */
    @ApiStatus.Internal
    @NotNull
    ElementFactory getElementFactory();

    /**
     * Runs a task in the next tick.
     *
     * @param task The task to run.
     */
    void nextTick(Runnable task);

    /**
     * The IFViewFrame for this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return The current framework that holds this view.
     * @throws UnsupportedOperationException If this view doesn't support a framework.
     */
    @ApiStatus.Internal
    IFViewFrame<?> getFramework();
}
