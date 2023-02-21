package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

public final class CloseInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        throw new UnsupportedOperationException();
    }
}
