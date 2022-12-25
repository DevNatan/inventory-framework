package me.saiintbrisson.minecraft.pipeline.interceptors;

import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.saiintbrisson.minecraft.AbstractView;
import me.saiintbrisson.minecraft.ViewItem;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the update phase of a context.
 */
public final class UpdateInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, VirtualView view) {
        if (!(view instanceof IFContext))
            throw new IllegalStateException("Update interceptor must be called with a context as subject.");

        final IFContext context = (IFContext) view;
        context.inventoryModificationTriggered();

        final AbstractView root = context.getRoot();

        // size of items in the context is proportional to the size of its container
        final int len = context.getContainer().getSize();

        for (int i = 0; i < len; i++) {
            // this resolve will get the items from both root and context, so we render both
            final ViewItem item = context.resolve(i, true);

            if (item == null) {
                final boolean modified = root.render(context, null, null, i);

                // default behavior if slot render wasn't modified
                if (!modified) context.getContainer().removeItem(i);

                continue;
            }

            root.update(context, item, i);
        }
    }
}
