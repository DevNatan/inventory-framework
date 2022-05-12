package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

/**
 * Handler to catch {@link VirtualView} errors.
 */
@FunctionalInterface
public interface ViewErrorHandler {

	/**
	 * Called when an error occurs.
	 *
	 * @param context   The current view context.
	 * @param exception The caught exception.
	 */
	void error(@NotNull ViewContext context, @NotNull Exception exception);

}
