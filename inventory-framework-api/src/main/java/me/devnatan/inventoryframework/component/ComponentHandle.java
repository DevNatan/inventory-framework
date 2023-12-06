package me.devnatan.inventoryframework.component;


import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.jetbrains.annotations.NotNull;

public abstract class ComponentHandle implements PipelineInterceptor<VirtualView> {

	/**
	 * Renders this component to the given context.
	 *
	 * @param context The context that this component will be rendered on.
	 */
	void rendered(@NotNull IFComponentRenderContext context) {}

	/**
	 * Called when this component is updated in the given context.
	 *
	 * @param context The update context.
	 */
	void updated(@NotNull IFComponentUpdateContext context) {}

	/**
	 * Clears this component from the given context.
	 *
	 * @param context The context that this component will be cleared from.
	 */
	public void cleared(@NotNull IFComponentContext context) {}

	/**
	 * Called when a viewer clicks in that component.
	 *
	 * @param context The click context.
	 */
	void clicked(@NotNull IFSlotClickContext context) {}
}
