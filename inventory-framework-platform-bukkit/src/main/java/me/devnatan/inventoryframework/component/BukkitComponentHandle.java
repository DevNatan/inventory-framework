package me.devnatan.inventoryframework.component;

import java.util.Objects;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;

public abstract class BukkitComponentHandle<T> extends PlatformComponentHandle<Context, T> {

    /**
     * Renders the component in the given context.
     *
     * @param context The context that this component will be rendered on.
     */
    protected abstract void rendered(ComponentRenderContext context);

    /**
     * Called when the component is updated in the given context.
     *
     * @param context The update context.
     */
    protected void updated(ComponentUpdateContext context) {}

    /**
     * Called when the component is cleared from the given context.
     *
     * @param context The context that this component will be cleared from.
     */
    protected void cleared(ComponentClearContext context) {}

    /**
     * Called when a viewer clicks in the component.
     *
     * @param context The click context.
     */
    protected void clicked(SlotClickContext context) {}

    @Override
    public final void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
        final PipelinePhase phase = Objects.requireNonNull(
                pipeline.getPhase(), "Pipeline phase cannot be null in ComponentHandle interceptor");

        if (phase == Component.RENDER) rendered((ComponentRenderContext) subject);
        if (phase == Component.UPDATE) updated((ComponentUpdateContext) subject);
        if (phase == Component.CLEAR) cleared((ComponentClearContext) subject);
        if (phase == Component.CLICK) clicked((SlotClickContext) subject);
    }
}
