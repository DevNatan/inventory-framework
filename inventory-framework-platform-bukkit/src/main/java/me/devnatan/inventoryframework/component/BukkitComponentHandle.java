package me.devnatan.inventoryframework.component;

import java.util.Objects;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.PublicPlatformRenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;

public abstract class BukkitComponentHandle<T> extends PlatformComponentHandle<Context, BukkitItemComponentBuilder> {

    @ApiStatus.OverrideOnly
    public abstract T builder();

    /**
     * Renders the component in the given context.
     *
     * @param render The context that this component will be rendered on.
     */
    protected abstract void rendered(PublicPlatformRenderContext render);

    /**
     * Called when the component is updated in the given context.
     *
     * @param update The update context.
     */
    protected void updated(ComponentUpdateContext update) {}

    /**
     * Called when the component is cleared from the given context.
     *
     * @param clear The context that this component will be cleared from.
     */
    protected void cleared(ComponentClearContext clear) {}

    /**
     * Called when a viewer clicks in the component.
     *
     * @param click The click context.
     */
    protected void clicked(SlotClickContext click) {}

    @Override
    public final void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        final PipelinePhase phase = Objects.requireNonNull(
                pipeline.getPhase(), "Pipeline phase cannot be null in ComponentHandle interceptor");

        if (phase == Component.RENDER) rendered((PublicPlatformRenderContext) subject);
        if (phase == Component.UPDATE) updated((ComponentUpdateContext) subject);
        if (phase == Component.CLEAR) cleared((ComponentClearContext) subject);
        if (phase == Component.CLICK) clicked((SlotClickContext) subject);
    }
}
