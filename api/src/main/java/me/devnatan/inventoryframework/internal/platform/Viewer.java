package me.devnatan.inventoryframework.internal.platform;

import org.jetbrains.annotations.NotNull;

public interface Viewer {

	/**
	 * Opens a container to this viewer.
	 *
	 * @param container The container that'll be opened.
	 */
	void open(@NotNull ViewContainer container);

	/**
	 * Closes the current container that this viewer is currently viewing.
	 */
	void close();
}
