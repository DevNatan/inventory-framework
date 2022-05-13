package me.saiintbrisson.minecraft;

import java.util.List;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ViewContext extends VirtualView {

	@NotNull
	List<Viewer> getViewers();

	void addViewer(@NotNull final Viewer viewer);

	void removeViewer(@NotNull final Viewer viewer);

	<T> T get(@NotNull final String key);

	<T> T get(@NotNull final String key, @NotNull Supplier<T> defaultValue);

	void set(@NotNull final String key, @NotNull final Object value);

	boolean has(@NotNull final String key);

	@NotNull
	ViewContainer getContainer();

	/**
	 * @deprecated Use {@link #getRoot()} instead.
	 */
	@Deprecated
	default View getView() {
		return (View) getRoot();
	}

	VirtualView getRoot();

	<T> PaginatedViewContext<T> paginated();

	String getUpdatedTitle();

	/**
	 * Updates the title of the container for the client of the player who owns this context.
	 * <p>
	 * This should not be used before the container is opened, if you need to set the
	 * __initial title__ use {@link OpenViewContext#setInventoryTitle(String)} on
	 * {@link View#onOpen(OpenViewContext)} instead.
	 * <p>
	 * This function is not agnostic, so it may be that your server version is not yet supported,
	 * if you try to use this function and fail (will possibly fail silently), report it to the
	 * inventory-framework developers to add support to your version.
	 *
	 * @param title The new container title.
	 */
	void updateTitle(@NotNull final String title);

	/**
	 * Updates the inventory title of the customer that owns this context to the initially defined title.
	 * Must be used after {@link #updateTitle(String)} to take effect.
	 */
	void resetTitle();

	/**
	 * If errors should be propagated to the view's error handler for that context.
	 *
	 * @return If errors will be propagated to the View.
	 */
	boolean isPropagateErrors();

	/**
	 * Defines whether errors should be propagated to the root view's error handler.
	 *
	 * @param propagateErrors If errors should be propagated to the root view.
	 */
	void setPropagateErrors(final boolean propagateErrors);

	Player getPlayer();

}
