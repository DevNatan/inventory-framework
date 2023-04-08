package me.devnatan.inventoryframework.pipeline;

import java.util.List;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the update phase of a context.
 */
public final class UpdateInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFContext)) return;
        if (subject instanceof IFOpenContext || subject instanceof IFCloseContext) return;

        final IFContext context = (IFContext) subject;
        final List<Component> componentList = context.getComponents();
        for (int i = 0; i < componentList.size(); i++) {
            final Component component = componentList.get(i);
            if (context.isMarkedForRemoval(i)) {
                component.clear(context);
                continue;
            }

            if (component.shouldBeUpdated()) {
                updateComponent(context, component);
            }
        }
    }

    /**
     * Renders a component in the given context.
     *
     * @param context   The context.
     * @param component The component that'll be rendered
     */
    public void updateComponent(@NotNull IFContext context, @NotNull Component component) {
        final IFSlotRenderContext renderContext = context.getRoot()
                .getElementFactory()
                .createSlotContext(
                        component.getPosition(),
                        component,
                        context.getContainer(),
                        ((IFConfinedContext) context).getViewer(),
                        context,
                        IFSlotRenderContext.class);

        component.updated(renderContext);

        if (!renderContext.isCancelled()) {
            component.render(renderContext);
        }
    }
}
