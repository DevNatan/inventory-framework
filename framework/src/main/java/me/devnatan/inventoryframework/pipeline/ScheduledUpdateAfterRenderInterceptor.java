package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.internal.Job;

public final class ScheduledUpdateAfterRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFContext)) return;

        final IFContext context = (IFContext) subject;
        final RootView root = context.getRoot();
        final long updateIntervalInTicks = context.getConfig().getUpdateIntervalInTicks();
        System.out.println("context = " + context);
        System.out.println("updateIntervalInTicks = " + updateIntervalInTicks);
        if (updateIntervalInTicks == 0) return;

        if (root.getScheduledUpdateJob() != null && root.getScheduledUpdateJob().isStarted()) return;

        final Job updateJob =
                root.getElementFactory().scheduleJobInterval(root, updateIntervalInTicks, context::update);
        updateJob.start();
        root.setScheduledUpdateJob(updateJob);
    }
}
