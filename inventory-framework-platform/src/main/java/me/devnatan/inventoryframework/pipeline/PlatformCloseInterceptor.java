package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFCloseContext;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class PlatformCloseInterceptor implements PipelineInterceptor<VirtualView> {

    @SuppressWarnings("rawtypes")
    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        final PlatformView root = (PlatformView) context.getRoot();
        root.onClose(context);
    }
}
