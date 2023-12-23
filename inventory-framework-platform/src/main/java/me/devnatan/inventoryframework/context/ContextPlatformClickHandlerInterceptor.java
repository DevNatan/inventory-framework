package me.devnatan.inventoryframework.context;

import static me.devnatan.inventoryframework.ViewConfig.CANCEL_ON_CLICK;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepted when a player clicks on the view container.
 * If the click is canceled, this interceptor ends the pipeline immediately.
 */
final class ContextPlatformClickHandlerInterceptor implements PipelineInterceptor<VirtualView> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;

        // inherit cancellation so we can un-cancel it
        click.setCancelled(click.isCancelled() || click.getConfig().isOptionSet(CANCEL_ON_CLICK, true));
        ((PlatformView) click.getRoot()).onClick(click);
    }
}
