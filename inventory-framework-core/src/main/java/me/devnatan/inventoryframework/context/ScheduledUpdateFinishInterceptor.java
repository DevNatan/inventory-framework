package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

final class ScheduledUpdateFinishInterceptor implements PipelineInterceptor<IFContext> {

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
