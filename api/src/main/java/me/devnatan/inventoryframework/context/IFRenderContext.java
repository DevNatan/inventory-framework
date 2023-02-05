package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewConfigBuilder;
import org.jetbrains.annotations.NotNull;

public interface IFRenderContext<TItem> extends IFConfinedContext {

	@NotNull
	TItem layoutSlot(String character);

	/**
	 * Adds an item to a specific slot in the context container.
	 *
	 * @param slot The slot in which the item will be positioned.
	 * @return A {@link TItem item builder} to configure the item.
	 */
	@NotNull
	TItem slot(int slot);

	/**
	 * Adds an item at the specific row and column in that context's container.
	 *
	 * @param row    The row in which the item will be positioned.
	 * @param column The column in which the item will be positioned.
	 * @return A {@link TItem item builder} to configure the item.
	 */
	@NotNull
	TItem slot(int row, int column);

	/**
	 * Adds an item to the first slot of this context's container.
	 *
	 * @return A {@link TItem item builder} to configure the item.
	 */
	@NotNull
	TItem firstSlot();

	/**
	 * Adds an item to the first slot of this context's container.
	 *
	 * @return A {@link TItem item builder} to configure the item.
	 */
	@NotNull
	TItem lastSlot();

	// TODO doc
	@NotNull
	TItem availableSlot();

	/**
	 * By default, all contexts inherit their root configuration. This allows access the current
	 * configuration with the possibility to change it only for that context.
	 * <p>
	 * Context configuration always takes precedence over root.
	 * <pre>{@code
	 * @Override
	 * public void onInit(ViewConfigBuilder config) {
	 *     config.cancelOnClick(); // cancels any click
	 * }
	 *
	 * @Override
	 * public void onFirstRender(ViewRenderContext ctx) {
	 *     ctx.config().isCancelOnClick(); // "true" inherited from root
	 *     ctx.config().cancelOnClick(); // allows click only for this context
	 * }
	 * }</pre>
	 * <p>
	 * Options that change the nature of the container are not allowed as the container has already
	 * been created at that point.
	 * <pre>{@code
	 * @Override
	 * public void onFirstRender(ViewRenderContext ctx) {
	 *     ctx.config().title("Woo"); // throws IllegalContainerModificationException
	 * }
	 * }</pre>
	 *
	 * @return The current context configuration.
	 */
	@NotNull
	ViewConfigBuilder config();
}
