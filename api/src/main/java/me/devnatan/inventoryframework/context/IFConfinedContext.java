package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;

/**
 * Represents a context whose only one or more viewers are always in the current scope of execution,
 * but they do not represent the entire collection of viewers in that context.
 */
public interface IFConfinedContext extends IFContext {

	/**
	 * Closes this context's container for the player in the current scope of execution.
	 */
	void closeForPlayer();

	/**
	 * Opens a new view only for the player that is in the execution context of that context.
	 * <p>
	 * This context will be immediately invalidated if there are no viewers left after opening.
	 *
	 * @param other The view to be opened.
	 */
	void openForPlayer(Class<? extends RootView> other);

}
