package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFCloseContext;

public final class ScheduledUpdateAfterCloseInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        if (context.isCancelled()) return;

        final RootView root = context.getRoot();
        if (root.getScheduledUpdateJob() == null
                || !root.getScheduledUpdateJob().isStarted()) return;

        // check possible shared context viewers count first to optimize global viewers count check
        if (!context.getViewers().isEmpty()) return;
        if (!root.getInternalContexts().stream()
                .allMatch(other -> other.getViewers().isEmpty())) return;

        root.getScheduledUpdateJob().cancel();
    }
}
