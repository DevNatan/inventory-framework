package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

public final class CloseInterceptor implements PipelineInterceptor<IFCloseContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFCloseContext> pipeline, IFCloseContext context) {
        final RootView root = context.getRoot();
        tryCallPlatformRootCloseHandler(root, context);

        final Viewer viewer = context.getViewer();
        if (context.isCancelled()) {
            // prevent platform-specific post-close handling since context was cancelled
            pipeline.finish();
            return;
        }

        context.removeViewer(viewer);

        if (canContextBeInvalidated(context)) {
            // TODO invalidate context
            root.removeContext(context);
        }
    }

    /**
     * Calls {@link PlatformView}'s close handler if <code>root</code> is a PlatformView.
     *
     * @param root    The platform root view.
     * @param context The subject context for the close handler.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void tryCallPlatformRootCloseHandler(RootView root, IFCloseContext context) {
        if (root instanceof PlatformView) {
            ((PlatformView) root).onClose(context);
        }
    }

    /**
     * Checks if a context can be invalidated by checking its viewers count.
     *
     * @param context The subject context.
     * @return If context can be invalidated.
     */
    private boolean canContextBeInvalidated(IFContext context) {
        return context.getViewers().isEmpty();
    }
}
