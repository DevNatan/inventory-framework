package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.component.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Created when a {@link Component} is about to be rendered.
 */
public interface IFComponentRenderContext extends IFContext {

	/**
	 * @return The rendered component.
	 */
	@NotNull
	Component getComponent();

	/**
	 * @return The container in which the component was rendered.
	 */
	@NotNull
	ViewContainer getRenderedContainer();

	/**
	 * @return If the component's rendering process was cancelled.
	 */
	boolean isCancelled();

	/**
	 * Defines if the component rendering process should be canceled.
	 *
	 * @param cancelled <code>true</code> to not render the component.
	 */
	void setCancelled(boolean cancelled);
}
