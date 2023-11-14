package me.devnatan.inventoryframework.context;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface IFComponentUpdateContext extends IFComponentContext, IFConfinedContext {

    @NotNull
    IFRenderContext getRoot();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    boolean isForceUpdate();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    void setForceUpdate(boolean forceUpdate);

    /**
     * If the component update event was cancelled.
     *
     * @return If the event was cancelled.
     */
    boolean isCancelled();

    /**
     * Cancels the component update event preventing the component to be updated.
     *
     * @param cancelled If component update must be cancelled.
     */
    void setCancelled(boolean cancelled);
}
