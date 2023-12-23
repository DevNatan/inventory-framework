package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;

final class ContextPlatformUpdateHandlerCallInterceptor implements PipelineInterceptor<IFContext> {

    @SuppressWarnings("unchecked")
    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (pipeline.getPhase() != PipelinePhase.Context.CONTEXT_UPDATE) return;

        @SuppressWarnings("rawtypes")
        final PlatformView root = (PlatformView) subject.getRoot();
        root.onUpdate(subject);
    }
}
