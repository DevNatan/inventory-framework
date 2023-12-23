package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

final class SlotClickToComponentClickCallInterceptor implements PipelineInterceptor<IFContext> {

    @Override
    public void intercept(@NotNull PipelineContext<IFContext> pipeline, @NotNull IFContext subject) {
        if (!(subject instanceof IFSlotClickContext)) return;

        final IFSlotClickContext click = (IFSlotClickContext) subject;
        final Component component = click.getComponent();

        if (component == null) return;

        click.performClickInComponent(
                component,
                click.isShared() ? null : click.getViewer(),
                click.getClickedContainer(),
                click.getPlatformEvent(),
                click.getClickedSlot(),
                click.isCombined());
    }
}
