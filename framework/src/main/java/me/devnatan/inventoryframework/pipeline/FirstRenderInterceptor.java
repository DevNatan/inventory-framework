package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;

/**
 * Intercepts the rendering phase of a context and renders all components on it.
 */
public final class FirstRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext context = (IFRenderContext) subject;
        registerComponents(context);

        final Viewer viewer = context.getViewer();
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

    private void registerComponents(IFRenderContext context) {
        final ElementFactory elementFactory = context.getRoot().getElementFactory();
        context.getRegisteredComponentBuilders().stream()
                .map(elementFactory::buildComponent)
                .forEach(context::addComponent);
    }
}
