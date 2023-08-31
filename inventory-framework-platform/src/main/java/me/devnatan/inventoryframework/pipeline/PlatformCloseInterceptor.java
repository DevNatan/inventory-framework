package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.PlatformRenderContext;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class PlatformCloseInterceptor implements PipelineInterceptor<VirtualView> {

    @SuppressWarnings("rawtypes")
    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        final PlatformRenderContext parent = (PlatformRenderContext) context.getParent();
        final PlatformView root = parent.getRoot();
        tryCallPlatformRootCloseHandler(root, context);

        if (context.isCancelled()) {
            pipeline.finish();
            return;
        }

        root.removeAndTryInvalidateContext(context.getViewer(), context);
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
}
