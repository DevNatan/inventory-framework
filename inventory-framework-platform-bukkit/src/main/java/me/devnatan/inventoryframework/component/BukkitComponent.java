package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentClearContext;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * Base class for components designed for the Bukkit platform.
 */
public abstract class BukkitComponent extends PlatformComponent<Context, BukkitDefaultComponentBuilder> {

	private ItemStack item;

	/**
     * Lifecycle event handler for the {@link PipelinePhase.Component#COMPONENT_RENDER} phase.
     * This method is called when the component needs to be rendered.
     *
     * @param render The render event as a context.
     */
    @ApiStatus.OverrideOnly
    protected abstract void onFirstRender(ComponentRenderContext render);

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

	// region Internal Implementation
	@Override
	final boolean render(IFComponentRenderContext context) {
		final ComponentRenderContext platformContext = (ComponentRenderContext) context;
		if (getRenderHandler() != null) {
			getRenderHandler().accept(context);
		}

		setPosition(platformContext.getSlot());
		setItem(platformContext.getItem());

		if (!isPositionSet())
			throw new IllegalStateException("Component position is not set. A position for the component must be "
				+ "assigned via #withSlot(...) in ComponentBuilder or programmatically before render");

		if (getItem() == null) {
			if (context.getContainer().getType().isResultSlot(getPosition())) {
				setVisible(true);
				return true;
			}

			// TODO This error must be in slot creation and not on render
			//      so the developer will know where the error is
			throw new IllegalStateException("At least one fallback item or render handler must be provided for "
				+ getClass().getName());
		}

		getContainer().renderItem(getPosition(), getItem());
		setVisible(true);
		return true;
	}

	@Override
	boolean clear(IFComponentClearContext context) {
		if (context.isCancelled()) return false;
		if (!isPositionSet()) return false;

		getContainer().removeItem(getPosition());
		return true;
	}

	// endregion

	// region Builder Methods
	public final ItemStack getItem() {
		return item;
	}

	public final void setItem(ItemStack item) {
		this.item = item;
	}

	public final boolean hasItem() {
		return getItem() != null;
	}
	// endregion
}
