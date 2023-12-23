package me.devnatan.inventoryframework.context.pipeline;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

public final class ContextPlatformRenderHandlerCallInterceptor implements PipelineInterceptor<IFContext> {

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFRenderContext)) return;
        ((PlatformView) subject.getRoot()).onFirstRender((IFRenderContext) subject);
    }
}
