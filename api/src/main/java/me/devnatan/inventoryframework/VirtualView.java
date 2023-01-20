package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFContext;

/**
 * VirtualView is the basis for creating a view it contains the implementation with methods that are
 * shared between regular views and contexts which are called "unified methods".
 * <p>
 * We call "view" a {@link VirtualView}, "root view" a {@link RootView} and implementations,
 * and "context" a {@link IFContext} and implementations.
 */
public interface VirtualView {

	/**
	 * The current title of this view.
	 *
	 * @return The current title of this view, if <code>null</code> will return the default title
	 * for this view type.
	 */
	String getTitle();

	ViewType getType();

	ViewType getTypeForCurrentPlatform();

}
