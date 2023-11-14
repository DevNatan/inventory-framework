package me.devnatan.inventoryframework;

import java.util.Set;
import java.util.UUID;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.Job;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface RootView extends VirtualView {

    /**
     * The unique identifier of this view.
     *
     * @return The unique id of this view.
     */
    @NotNull
    UUID getUniqueId();

    /**
     * All contexts linked to this view.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return A set of all currently active contexts in this view.
     */
    @ApiStatus.Internal
    @UnmodifiableView
    Set<IFContext> getInternalContexts();

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
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param task The task to run.
     */
    @ApiStatus.Internal
    void nextTick(Runnable task);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    Job getScheduledUpdateJob();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void setScheduledUpdateJob(@NotNull Job job);

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void invalidateEndlessContext(String contextId);
}
