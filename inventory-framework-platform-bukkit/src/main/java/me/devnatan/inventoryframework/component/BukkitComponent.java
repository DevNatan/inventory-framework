package me.devnatan.inventoryframework.component;

import static me.devnatan.inventoryframework.pipeline.PipelinePhase.Component.COMPONENT_CLEAR;
import static me.devnatan.inventoryframework.pipeline.PipelinePhase.Component.COMPONENT_CLICK;
import static me.devnatan.inventoryframework.pipeline.PipelinePhase.Component.COMPONENT_RENDER;
import static me.devnatan.inventoryframework.pipeline.PipelinePhase.Component.COMPONENT_UPDATE;

import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for components designed for the Bukkit platform.
 */
public abstract class BukkitComponent extends PlatformComponent<Context, BukkitItemComponentBuilder> {

    /**
     * Constructs a new BukkitComponent.
     * Initializes and sets up the pipeline for handling lifecycle events.
     */
    protected BukkitComponent() {
        super();
        final Pipeline<IFComponentContext> pipeline = getPipeline();

        pipeline.intercept(COMPONENT_RENDER, ($, ctx) -> onRender((ComponentRenderContext) ctx));
        pipeline.intercept(COMPONENT_UPDATE, ($, ctx) -> onUpdate((ComponentUpdateContext) ctx));
        pipeline.intercept(COMPONENT_CLICK, ($, ctx) -> onClick((SlotClickContext) ctx));
        pipeline.intercept(COMPONENT_CLEAR, ($, ctx) -> onClear((ComponentClearContext) ctx));
    }

    /**
     * Lifecycle event handler for the {@link PipelinePhase.Component#COMPONENT_RENDER} phase.
     * This method is called when the component needs to be rendered.
     *
     * @param render The render event as a context.
     */
    @ApiStatus.OverrideOnly
    protected abstract void onRender(ComponentRenderContext render);

    /**
     * Lifecycle event handler for the {@link PipelinePhase.Component#COMPONENT_UPDATE} phase.
     * This method is called when the component needs to be updated.
     *
     * @param update The update event as a context.
     */
    @ApiStatus.OverrideOnly
    protected void onUpdate(ComponentUpdateContext update) {}

    /**
     * Lifecycle event handler for the {@link PipelinePhase.Component#COMPONENT_CLICK} phase.
     * This method is called when the component is clicked.
     * <p>
     * You can retrieve the component instance from {@link SlotClickContext#getComponent()} as well.
     *
     * @param click The click event as a context.
     */
    @ApiStatus.OverrideOnly
    protected void onClick(SlotClickContext click) {}

    /**
     * Lifecycle event handler for the {@link PipelinePhase.Component#COMPONENT_CLEAR} phase.
     * This method is called when the component needs to be cleared.
     *
     * @param clear The clear event as a context.
     */
    @ApiStatus.OverrideOnly
    protected void onClear(ComponentClearContext clear) {}
}
