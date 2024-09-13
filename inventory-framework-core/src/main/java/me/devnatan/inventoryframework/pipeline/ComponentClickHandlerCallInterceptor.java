package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.IFDebug;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.jetbrains.annotations.NotNull;

public final class ComponentClickHandlerCallInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;
        final Component component = click.getComponent();

        if (component == null) {
            IFDebug.debug("ComponentClickHandlerCallInterceptor: null component");
            return;
        }
        IFDebug.debug("ComponentClickHandlerCallInterceptor: %s", component);
        component.clicked(click);
    }
}
