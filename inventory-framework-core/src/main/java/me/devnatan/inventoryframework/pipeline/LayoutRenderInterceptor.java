package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.jdk.IndexSlotFunction;

public final class LayoutRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext renderContext = (IFRenderContext) subject;
        for (final LayoutSlot layoutSlot : renderContext.getLayoutSlots()) {
            final IndexSlotFunction<ComponentFactory> elementsFactory = layoutSlot.getFactory();
            if (elementsFactory == null) {
                if (layoutSlot.isDefinedByTheUser())
                    throw new InventoryFrameworkException(
                            "#layoutSlot(...) factory cannot be null when defined by the user");
                continue;
            }

            int iterationIndex = 0;
            for (final int slot : layoutSlot.getPositions()) {
                final ComponentFactory factory = elementsFactory.apply(iterationIndex++, slot);
                final Component component = factory.create();
                renderContext.addComponent(component);
            }
        }
    }
}
