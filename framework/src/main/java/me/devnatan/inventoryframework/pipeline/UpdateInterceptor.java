package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.IFItem;
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
        System.out.println("update intercepted");
        for (final Component component : context.getComponents()) {
            if (component.isMarkedForRemoval()) {
                component.clear(context);
                System.out.println("markedForRemoval: " + component);
                continue;
            }

            if (!shouldBeUpdated(component)) {
                System.out.println("shouldNotBeUpdated: " + component);
                continue;
            }

            renderComponent(context, component);
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

    /**
     * Renders a component in the given context.
     *
     * @param context   The context.
     * @param component The component that'll be rendered
     */
    public void renderComponent(@NotNull IFContext context, @NotNull Component component) {
        System.out.println("renderComponent: " + component);
        final IFSlotRenderContext renderContext = context.getRoot()
                .getElementFactory()
                .createSlotContext(
                        component.getPosition(),
                        component,
                        context.getContainer(),
                        ((IFConfinedContext) context).getViewer(),
                        context,
                        IFSlotRenderContext.class);

        component.render(renderContext);
    }
}
