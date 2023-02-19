package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the rendering phase of a context and renders all components on it.
 */
public final class RenderInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        final RootView root = context.getRoot();
        final int len = context.getContainer().getSize();

        for (int i = 0; i < len; i++) {
            final Component component = context.getComponent(i);
            if (component == null) {
                context.getContainer().removeItem(i);
                continue;
            }

            root.renderComponent(context, component);
        }
    }
}
