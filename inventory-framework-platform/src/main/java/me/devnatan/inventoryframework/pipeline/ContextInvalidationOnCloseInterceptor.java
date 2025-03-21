package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFCloseContext;

public final class ContextInvalidationOnCloseInterceptor implements PipelineInterceptor<VirtualView> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        if (!context.isActive() || context.isCancelled()) {
            IFDebug.debug(
                    "Invalidation skipped (active = %b, cancelled = %b)", context.isActive(), context.isCancelled());
            return;
        }

        final PlatformView root = (PlatformView) context.getRoot();
        final Viewer viewer = context.getViewer();
        root.onViewerRemoved(context.getParent(), viewer.getPlatformInstance());
        root.getPipeline().execute(StandardPipelinePhases.VIEWER_REMOVED, subject);
        root.removeAndTryInvalidateContext(viewer, context);
    }
}
