package me.devnatan.inventoryframework.context.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unchecked")
public final class ContextPlatformCloseHandlerCallInterceptor implements PipelineInterceptor<IFContext> {

    @SuppressWarnings("rawtypes")
    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFCloseContext)) return;

        final IFCloseContext context = (IFCloseContext) subject;
        final PlatformView root = (PlatformView) context.getRoot();
        root.onClose(context);
    }
}
