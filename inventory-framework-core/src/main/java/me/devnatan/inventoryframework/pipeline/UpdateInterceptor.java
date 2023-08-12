package me.devnatan.inventoryframework.pipeline;

import java.util.List;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;

/**
 * Intercepts the update phase of a context.
 */
public final class UpdateInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFContext context = (IFContext) subject;
        final List<Component> componentList = context.getComponents();
        for (int i = 0; i < componentList.size(); i++) {
            final Component component = componentList.get(i);
            if (context.isMarkedForRemoval(i)) {
                component.clear(context);
                continue;
            }

            context.updateComponent(component);
        }
    }
}
