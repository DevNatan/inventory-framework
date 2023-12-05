package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewContainer;
import org.jetbrains.annotations.NotNull;

public interface IFComponentRenderContext extends IFComponentContext, IFConfinedContext {

    ViewContainer getContainer();
}
