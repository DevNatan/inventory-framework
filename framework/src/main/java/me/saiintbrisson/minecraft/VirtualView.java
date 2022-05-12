package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VirtualView {

	@NotNull
	List<Viewer> getViewers();

	@NotNull
	String getTitle();

	/**
	 * Returns the row count of this view.
	 *
	 * @return The row count of this view.
	 */
	int getRows();

	/**
	 * Close this view.
	 */
	void close();

//	/**
//	 * Renders an item in this view.
//	 *
//	 * @param slot The slot that the item will be rendered.
//	 */
//	void render(@NotNull ViewItem item, int slot);

}
