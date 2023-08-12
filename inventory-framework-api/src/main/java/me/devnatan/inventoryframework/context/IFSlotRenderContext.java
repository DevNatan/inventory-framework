package me.devnatan.inventoryframework.context;

import org.jetbrains.annotations.ApiStatus;

public interface IFSlotRenderContext extends IFSlotContext {

    @ApiStatus.Internal
    Object getResult();

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
