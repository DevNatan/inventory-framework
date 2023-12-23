package me.devnatan.inventoryframework.context.pipeline;

import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

public final class ViewerLastInteractionTrackerInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, @NotNull IFContext subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;

        // fast path -- skip checks and currentTimeMillis() calls if interaction delay is not enabled
        if (click.getConfig().getInteractionDelayInMillis() <= 0) return;

        final Viewer viewer = click.getViewer();
        if (!click.isCombined() && viewer.isBlockedByInteractionDelay()) {
            IFDebug.debug("Pipeline finished due to interaction delay");
            click.setCancelled(true);
            pipeline.finish();
            return;
        }

        viewer.setLastInteractionInMillis(System.currentTimeMillis());
    }
}
