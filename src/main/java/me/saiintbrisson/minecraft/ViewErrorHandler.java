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
	 * @param exception The error.
	 */
	void error(@NotNull Exception exception);

}
