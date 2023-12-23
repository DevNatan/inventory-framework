package me.devnatan.inventoryframework.context.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

public final class ContextInvalidateInterceptor implements PipelineInterceptor<IFContext> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        if (!context.isActive() || context.isCancelled()) return;

        final PlatformView root = (PlatformView) context.getRoot();
        final Viewer viewer = context.getViewer();
        root.onViewerRemoved(context.getParent(), viewer.getPlatformInstance());
        root.removeAndTryInvalidateContext(viewer, context);
    }
}
