package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.ApiStatus;

public abstract class BukkitComponent extends PlatformComponent<Context, BukkitItemComponentBuilder> {

	protected BukkitComponent() {
		super();
		final Pipeline<IFComponentContext> pipeline = getPipeline();
		pipeline.intercept(PipelinePhase.Component.COMPONENT_RENDER, ($, ctx) -> onRender((ComponentRenderContext) ctx));
		pipeline.intercept(PipelinePhase.Component.COMPONENT_UPDATE, ($, ctx) -> onUpdate((ComponentUpdateContext) ctx));
		pipeline.intercept(PipelinePhase.Component.COMPONENT_CLICK, ($, ctx) -> onClick((SlotClickContext) ctx));
		pipeline.intercept(PipelinePhase.Component.COMPONENT_CLEAR, ($, ctx) -> onClear((ComponentClearContext) ctx));
	}

	@ApiStatus.OverrideOnly
	protected abstract void onRender(ComponentRenderContext context);

	@ApiStatus.OverrideOnly
	protected void onUpdate(ComponentUpdateContext context) {}

	@ApiStatus.OverrideOnly
	protected void onClick(SlotClickContext context) {}

	@ApiStatus.OverrideOnly
	protected void onClear(ComponentClearContext context) {}
}
