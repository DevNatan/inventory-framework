package me.devnatan.inventoryframework.context;

public interface IFSlotRenderContext extends IFSlotContext {

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}