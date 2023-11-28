package me.devnatan.inventoryframework.pipeline;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.jetbrains.annotations.NotNull;

public final class ComponentClickHandlerCallInterceptor implements PipelineInterceptor<VirtualView> {

    @Override
    public void intercept(@NotNull PipelineContext<VirtualView> pipeline, @NotNull VirtualView subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;
        click.getComponent().getInteractionHandler().clicked(click.getComponent(), click);
    }
}
