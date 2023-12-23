package me.devnatan.inventoryframework.context.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.Job;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

public final class ScheduledUpdateStartInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext context) {
        if (!(context instanceof IFRenderContext)) return;

        final RootView root = (RootView) context.getRoot();
        final long updateIntervalInTicks = context.getConfig().getUpdateIntervalInTicks();

        if (updateIntervalInTicks == 0) return;
        if (root.getScheduledUpdateJob() != null && root.getScheduledUpdateJob().isStarted()) return;

        final Job updateJob = root.getElementFactory()
                .scheduleJobInterval(root, updateIntervalInTicks, () -> root.getInternalContexts().stream()
                        .filter(IFContext::isActive)
                        .forEach(IFContext::update));
        updateJob.start();
        root.setScheduledUpdateJob(updateJob);
    }
}
