package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.jetbrains.annotations.NotNull;

public final class ViewerLastInteractionTrackerInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;
        final Viewer viewer = click.getViewer();
        if (viewer.isBlockedByInteractionDelay()) {
            click.setCancelled(true);
            pipeline.finish();
            return;
        }

        viewer.setLastInteractionInMillis(System.currentTimeMillis());
    }
}
