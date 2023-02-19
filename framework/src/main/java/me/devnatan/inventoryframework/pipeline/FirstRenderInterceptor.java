package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the rendering phase of a context and renders all components on it.
 */
public final class FirstRenderInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        final int len = context.getContainer().getSize();

        for (int i = 0; i < len; i++) {
            final Component component = context.getComponent(i);
            if (component == null) {
                context.getContainer().removeItem(i);
                continue;
            }

            final IFSlotRenderContext renderContext = context.getRoot()
                    .getElementFactory()
                    .createSlotContext(
                            i,
                            component,
                            context.getContainer(),
                            ((IFConfinedContext) context).getViewer(),
                            context,
                            IFSlotRenderContext.class);

            component.render(renderContext);
        }
    }
}
