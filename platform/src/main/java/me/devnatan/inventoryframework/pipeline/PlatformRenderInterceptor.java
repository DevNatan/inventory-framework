package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFRenderContext;
import org.jetbrains.annotations.NotNull;

public final class PlatformRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        if (!(view instanceof IFRenderContext)) return;

        final PlatformView root = (PlatformView) ((IFRenderContext) view).getRoot();
        final IFRenderContext context = (IFRenderContext) view;
        root.onFirstRender(context);
    }
}
