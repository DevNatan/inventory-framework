package me.devnatan.inventoryframework.pipeline;

import java.util.List;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ItemComponent;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the update phase of a context.
 */
public final class UpdateInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        final List<Component> componentList = context.getComponents();
        for (int i = 0; i < componentList.size(); i++) {
            final Component component = componentList.get(i);
            if (context.isMarkedForRemoval(i)) {
                component.clear(context);
                continue;
            }

            if (!shouldBeUpdated(component)) continue;

            updateComponent(context, component);
        }
    }

    /**
     * Determines if a component should be updated.
     *
     * @param component The component.
     * @return {@code true} if component should be updated or {@code false} otherwise.
     */
    private boolean shouldBeUpdated(@NotNull Component component) {
        if (component instanceof ItemComponent) {
            final ItemComponent item = (ItemComponent) component;

            // items without a render or update handler are ignored because the fallback item is
            // only rendered once in the initial rendering phase
            return item.getRenderHandler() != null;
        }

        return true;
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

        if (renderContext.isCancelled()) return;

        component.render(renderContext);
    }
}
