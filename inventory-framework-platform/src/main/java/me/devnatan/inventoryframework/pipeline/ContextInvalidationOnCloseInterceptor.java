package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFCloseContext;

public final class ContextInvalidationOnCloseInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        if (!context.isActive() || context.isCancelled()) return;

        ((PlatformView) context.getRoot()).removeAndTryInvalidateContext(context.getViewer(), context);
    }
}
