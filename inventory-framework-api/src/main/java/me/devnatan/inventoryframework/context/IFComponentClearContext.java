package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewContainer;

public interface IFComponentClearContext extends IFComponentContext, IFConfinedContext {

    IFRenderContext getParent();

    ViewContainer getContainer();

    boolean isCancelled();

    void setCancelled(boolean cancelled);
}
