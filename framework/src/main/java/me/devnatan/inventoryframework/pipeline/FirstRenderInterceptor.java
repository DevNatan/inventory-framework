package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the rendering phase of a context and renders all components on it.
 */
public final class FirstRenderInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        final Viewer viewer = ((IFConfinedContext) context).getViewer();
        final ElementFactory elementFactory = context.getRoot().getElementFactory();

        for (final Component component : context.getComponents()) {
            final IFSlotRenderContext slotRenderContext = elementFactory.createSlotContext(
                    component.getPosition(),
                    component,
                    context.getContainer(),
                    viewer,
                    context,
                    IFSlotRenderContext.class);
            component.render(slotRenderContext);
        }
    }
}
