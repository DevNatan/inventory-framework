package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PlatformViewFrame<V, P, F extends PlatformViewFrame<V, P, F>> extends FeatureInstaller<P> {

	/**
	 * Registers a new view to this view frame.
	 *
	 * @param views The views that'll be registered.
	 * @return This platform view frame.
	 */
	F with(@NotNull AbstractView... views);

	/**
	 * Removes a view from this view frame.
	 *
	 * @param views The views that'll be removed.
	 * @return This platform view frame.
	 */
	F remove(@NotNull AbstractView... views);

	/**
	 * Registers this view frame.
	 *
	 * @throws IllegalStateException If this view frame is already registered.
	 * @return This platform view frame.
	 */
	F register();

	/**
	 * Unregisters this view frame and closes all registered views.
	 *
	 * @throws IllegalStateException If this view frame is not registered.
	 */
	void unregister();

	/**
	 * Returns `true` if this view frame is registered or `false` otherwise.
	 *
	 * @return If this view frame is registered.
	 */
	boolean isRegistered();

	@NotNull
	P getOwner();

	@NotNull
	ViewComponentFactory getFactory();

	<R extends AbstractView> R open(
		@NotNull Class<R> viewClass,
		@NotNull V viewer
	);

	<R extends AbstractView> R open(
		@NotNull Class<R> viewClass,
		@NotNull V viewer,
		Map<String, Object> data
	);

	ViewErrorHandler getErrorHandler();

	void setErrorHandler(ViewErrorHandler errorHandler);

	void nextTick(Runnable runnable);

	Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem();

	/**
	 * @deprecated Use {@link #setNavigateBackItemFactory(BiConsumer)} instead.
	 */
	@Deprecated
	void setDefaultPreviousPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItemFactory);

	F setNavigateBackItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateBackItemFactory);

	Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem();

	/**
	 * @deprecated Use {@link #setNavigateNextItemFactory(BiConsumer)} instead.
	 */
	@Deprecated
	void setDefaultNextPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItemFactory);

	F setNavigateNextItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateNextItemFactory);

}
