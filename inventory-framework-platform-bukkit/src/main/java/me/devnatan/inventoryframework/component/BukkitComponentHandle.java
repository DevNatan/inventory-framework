package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import org.jetbrains.annotations.NotNull;

public abstract class BukkitComponentHandle<T> extends AbstractComponentHandle<Context, T> {

	protected abstract void rendered(ComponentRenderContext context);

	protected void updated(ComponentUpdateContext context) {}

	protected void cleared(RenderContext context) {}

	protected void clicked(SlotClickContext context) {}

	@Override
	public final void intercept(PipelineContext<VirtualView> pipeline, VirtualView subject) {
		if (pipeline.getPhase() == Component.RENDER) rendered((ComponentRenderContext) subject);
		if (pipeline.getPhase() == Component.UPDATE) updated((ComponentUpdateContext) subject);
		if (pipeline.getPhase() == Component.CLICK) clicked((SlotClickContext) subject);
		if (pipeline.getPhase() == Component.CLEAR) cleared((RenderContext) subject);
	}
}
