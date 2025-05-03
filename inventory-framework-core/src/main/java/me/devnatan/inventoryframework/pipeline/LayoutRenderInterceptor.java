package me.devnatan.inventoryframework.pipeline;

import java.util.function.IntFunction;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentFactory;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;

public final class LayoutRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext renderContext = (IFRenderContext) subject;
        for (final LayoutSlot layoutSlot : renderContext.getLayoutSlots()) {
            final IntFunction<ComponentFactory> factory = layoutSlot.getFactory();
            if (factory == null) {
                if (layoutSlot.isDefinedByTheUser())
                    throw new InventoryFrameworkException(
                            "#layoutSlot(...) factory cannot be null when defined by the user");
                continue;
            }

			if (layoutSlot.isToFill()) {
				int iterationIndex = 0;
				for (final int slot : layoutSlot.getPositions()) {
					renderContext.addComponent(createComponent(slot, iterationIndex++, factory));
				}
			} else {
				final int nextAvailableSlot = layoutSlot.
				renderContext.addComponent(createComponent(nextAvailableSlot, 0 /* TODO not supported for now */, factory));
			}
        }
    }

	private Component createComponent(int slot, int index, IntFunction<ComponentFactory> factory) {
		final ComponentFactory componentFactory = factory.apply(index);
		if (componentFactory instanceof ItemComponentBuilder)
			((ItemComponentBuilder<?, ?>) componentFactory).withSlot(slot);

		return componentFactory.create();
	}
}
