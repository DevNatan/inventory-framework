package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFConfinedContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import org.jetbrains.annotations.NotNull;

/**
 * Intercepts the update phase of a context.
 */
public final class UpdateInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, IFContext context) {
        final int len = context.getContainer().getSize();

        for (int i = 0; i < len; i++) {
            final Component component = context.getComponent(i);
            if (component == null) {
                context.getContainer().removeItem(i);
                continue;
            }

            if (!shouldBeUpdated(component)) continue;

            renderComponent(context, component, i);
        }
    }

    /**
     * Determines if a component should be updated.
     *
     * @param component The component.
     * @return {@code true} if component should be updated or {@code false} otherwise.
     */
    private boolean shouldBeUpdated(@NotNull Component component) {
        if (component instanceof IFItem) {
            final IFItem<?> item = (IFItem<?>) component;

            // items without a render handler are ignored because the fallback item is only rendered
            // once in the initial rendering phase
            return item.getRenderHandler() != null;
        }

        return true;
    }

    public void renderComponent(@NotNull IFContext context, @NotNull Component component, int index) {
        final IFSlotRenderContext renderContext = context.getRoot()
                .getElementFactory()
                .createSlotContext(
                        index,
                        component,
                        context.getContainer(),
                        ((IFConfinedContext) context).getViewer(),
                        context,
                        IFSlotRenderContext.class);

        component.render(renderContext);
    }
}
