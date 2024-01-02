package me.devnatan.inventoryframework.context;

import java.util.List;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

/**
 * Intercepts the update phase of a context.
 */
final class ContextUpdateInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        if (!context.isRendered()) return;

        final List<Component> componentList = context.getComponents();
        for (final Component component : componentList) {
            // TODO Set update reason
            context.updateComponent(component, false, null);
        }
    }
}
