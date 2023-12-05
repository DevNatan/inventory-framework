package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewContainer;

public interface IFComponentRenderContext extends IFComponentContext, IFConfinedContext {

    IFRenderContext getParent();

    ViewContainer getContainer();
}
