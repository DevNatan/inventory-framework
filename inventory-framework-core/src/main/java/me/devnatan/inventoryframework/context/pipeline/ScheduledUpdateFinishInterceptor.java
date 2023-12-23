package me.devnatan.inventoryframework.context.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

public final class ScheduledUpdateFinishInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        final RootView root = (RootView) context.getRoot();
        if (root.getScheduledUpdateJob() == null
                || !root.getScheduledUpdateJob().isStarted()) return;
        if (context.isCancelled() || !context.isActive()) return;
        if (root.getInternalContexts().stream().noneMatch(IFContext::isActive)) return;

        root.getScheduledUpdateJob().cancel();
    }
}
