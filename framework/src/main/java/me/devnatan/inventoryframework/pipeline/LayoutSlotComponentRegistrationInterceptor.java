package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.ElementFactory;
import me.devnatan.inventoryframework.internal.LayoutSlot;

public class LayoutSlotComponentRegistrationInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext context) {
        if (!(context instanceof IFRenderContext))
            throw new IllegalArgumentException("LayoutRenderInterceptor subject must be render context");

        final ElementFactory elementFactory = context.getRoot().getElementFactory();
        for (final LayoutSlot layoutSlot : ((IFRenderContext) context).getLayoutSlots()) {
            int iterationIndex = 0;
            for (final int slot : layoutSlot.getSlots()) {
                final ComponentBuilder<?> builder = layoutSlot.getFactory().apply(iterationIndex++);
                if (builder instanceof ItemComponentBuilder) ((ItemComponentBuilder<?>) builder).withSlot(slot);

                final Component component = elementFactory.buildComponent(builder);
                context.addComponent(component);
            }
        }
    }
}
