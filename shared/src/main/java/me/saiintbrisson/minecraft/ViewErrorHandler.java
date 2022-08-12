package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

/**
 * Handler to catch view errors.
 */
@FunctionalInterface
public interface ViewErrorHandler {

	/**
	 * Called when an error occurs.
	 *
	 * @param context   The current view context.
	 * @param exception The caught exception.
	 */
	void error(ViewContext context, @NotNull Exception exception) throws Exception;
}
