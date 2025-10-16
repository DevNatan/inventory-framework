package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface IFCloseContext extends IFConfinedContext {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    IFRenderContext getParent();

    /**
     * Cancellation state of that context.
     *
     * @return If that context was cancelled.
     */
    boolean isCancelled();

    /**
     * Cancels or not this context.
     * <p>
     * The side effect of canceling a close context is determined by the current platform.
     * <p>
     * NOOP (does nothing) if close context cancellation is not supported.
     *
     * @param cancelled If this context should be cancelled.
     */
    void setCancelled(boolean cancelled);

    /**
     * The container of this context.
     *
     * @return The container of this context.
     */
    @NotNull
    ViewContainer getContainer();

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    Object getPlatformEvent();
}
