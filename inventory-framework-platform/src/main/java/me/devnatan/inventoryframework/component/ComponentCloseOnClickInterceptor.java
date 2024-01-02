package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

/**
 * Intercepted when a player clicks on an item the view container. Checks if the container should be
 * closed when the item is clicked.
 */
final class ComponentCloseOnClickInterceptor implements PipelineInterceptor<IFComponentContext> {

    @Override
    public void intercept(PipelineContext<IFComponentContext> pipeline, IFComponentContext subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;
        if (click.isOutsideClick()) return;

        final PlatformComponent component = (PlatformComponent) click.getComponent();
        if (!component.isVisible() || !component.isCloseOnClick()) return;

        click.closeForPlayer();
    }
}
