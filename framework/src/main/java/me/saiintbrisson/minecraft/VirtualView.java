package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface VirtualView {

	@NotNull
	List<Viewer> getViewers();

	/**
	 * The current title of this view's container.
	 * @return The title of container of this view.
	 */
	@NotNull
	String getTitle();

	/**
	 * Returns the row count of this view.
	 *
	 * @return The row count of this view.
	 */
	int getRows();

	/**
	 * Closes this view.
	 */
	void close();

}
