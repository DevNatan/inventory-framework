package me.devnatan.inventoryframework.context;

import java.util.function.IntFunction;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import me.devnatan.inventoryframework.internal.LayoutSlot;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;

final class LayoutRenderInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(PipelineContext<IFContext> pipeline, IFContext subject) {
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
                    final PlatformComponentBuilder<?, ?> builder =
                            (PlatformComponentBuilder<?, ?>) builderFactory.apply(index);
                    builder.withSlot(slot);

                    component = builder.internalBuildComponent(renderContext);
                } else component = componentFactory.apply(index);

                renderContext.addComponent(component);
                index++;
            }
        }
    }
}
