package me.devnatan.inventoryframework.context;

import org.jetbrains.annotations.ApiStatus;

public interface IFSlotRenderContext extends IFSlotContext, IFConfinedContext {

    @ApiStatus.Internal
    Object getResult();

    boolean isCancelled();

    void setCancelled(boolean cancelled);

    /**
     * Checks if the item in this context has been changed.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return If the item in this context has been changed.
     */
    @ApiStatus.Internal
    boolean hasChanged();

    /**
     * Marks this context as changed.
     *
     * <p>Improperly changing this property can cause unexpected side effects.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param changed If the context item was changed.
     */
    @ApiStatus.Internal
    void setChanged(boolean changed);
}
