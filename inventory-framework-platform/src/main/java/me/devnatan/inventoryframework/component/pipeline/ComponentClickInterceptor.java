package me.devnatan.inventoryframework.component.pipeline;

import me.devnatan.inventoryframework.component.PlatformComponent;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on an item the view container.
 */
public final class ComponentClickInterceptor implements PipelineInterceptor<IFComponentContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFComponentContext> pipeline, @NotNull IFComponentContext subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;
        if (click.isOutsideClick()) return;

        final PlatformComponent component = (PlatformComponent) click.getComponent();

        // inherit cancellation so we can un-cancel it
        click.setCancelled(component.isCancelOnClick());
    }
}
