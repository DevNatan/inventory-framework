package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.jetbrains.annotations.NotNull;

public final class ViewerLastInteractionUpdaterInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final Viewer viewer = ((IFSlotClickContext) subject).getViewer();
        viewer.setLastInteractionInMillis(System.currentTimeMillis());
    }
}
