package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;

public final class ScheduledUpdateAfterCloseInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        final RootView root = context.getRoot();
        if (root.getScheduledUpdateJob() == null
                || !root.getScheduledUpdateJob().isStarted()) return;
        if (context.isCancelled() || !context.isActive()) return;
        if (root.getInternalContexts().stream().noneMatch(IFContext::isActive)) return;

        root.getScheduledUpdateJob().cancel();
    }
}
