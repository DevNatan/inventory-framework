package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.VirtualView;
import me.saiintbrisson.minecraft.AbstractView;
import me.devnatan.inventoryframework.IFItem;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the rendering phase of a context and renders {@link VirtualView#slot(int) statically defined items}
 * in it and its root.
 */
public final class RenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        if (!(view instanceof IFContext))
            throw new IllegalStateException("Render interceptor must be called with a context as subject.");

        final IFContext context = (IFContext) view;
        context.inventoryModificationTriggered();

        final AbstractView root = context.getRoot();

        // we prioritize the amount of items in the context because the priority is in the context
        // and the size of items in the context is proportional to the size of its container
        final int len = context.getContainer().getSize();

        for (int i = 0; i < len; i++) {
            // this resolve will get the items from both root and context, so we render both
            final IFItem item = context.resolve(i, true);
            if (item == null) {
                root.render(context, null, null, i);
                continue;
            }

            root.render(context, item, i);
        }
    }
}
