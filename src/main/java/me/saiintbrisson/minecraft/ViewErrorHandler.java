package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

/**
 * Handler to catch View errors.
 *
 * @author Natan Vieira
 */
@FunctionalInterface
public interface ViewErrorHandler {

	/**
	 * Called when an error occurs in some View handler.
	 *
	 * @param context The current view context.
	 * @param exception The caught exception.
	 */
	void error(@NotNull ViewContext context, @NotNull Exception exception);

}
