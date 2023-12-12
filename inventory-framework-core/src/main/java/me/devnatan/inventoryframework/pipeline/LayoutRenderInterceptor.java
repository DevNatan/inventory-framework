package me.devnatan.inventoryframework.pipeline;

import java.util.function.IntFunction;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.LayoutSlot;

public final class LayoutRenderInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        if (!(subject instanceof IFRenderContext)) return;

        final IFRenderContext renderContext = (IFRenderContext) subject;
        for (final LayoutSlot layoutSlot : renderContext.getLayoutSlots()) {
            final IntFunction<Component> componentFactory = layoutSlot.getComponentFactory();
            final IntFunction<ComponentBuilder> builderFactory = layoutSlot.getBuilderFactory();
            if (builderFactory == null && componentFactory == null) {
                if (layoutSlot.isDefinedByTheUser())
                    throw new InventoryFrameworkException(
                            "#layoutSlot(...) factory cannot be null when defined by the user");
                continue;
            }

            int index = 0;
            for (final int slot : layoutSlot.getPositions()) {
                final Component component;
                if (builderFactory != null) {
                    final ComponentBuilder factory = builderFactory.apply(index);
                    if (factory instanceof ItemComponentBuilder)
                        ((ItemComponentBuilder) componentFactory).setPosition(slot);

                    component = factory.buildComponent(renderContext);
                } else {
                    component = componentFactory.apply(index);
                }

                renderContext.addComponent(component);
                index++;
            }
        }
    }
}
