package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface PlatformViewFrame<V, P, F extends PlatformViewFrame<V, P, F>> {

	/**
	 * Registers a new view to this view frame.
	 *
	 * @param views The views that'll be registered.
	 * @return This view frame instance.
	 */
	F with(@NotNull final View... views);

	/**
	 * Removes a view from this view frame.
	 *
	 * @param views The views that'll be removed.
	 */
	void remove(@NotNull final View... views);

	/**
	 * Registers this view frame.
	 *
	 * @throws IllegalStateException If this view frame is already registered.
	 */
	void register();

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
	ViewContainerFactory getFactory();

	<R extends View> R open(
		@NotNull final Class<R> viewClass,
		@NotNull final V viewer
	);

	<R extends View> R open(
		@NotNull final Class<R> viewClass,
		@NotNull final V viewer,
		@NotNull final Map<String, Object> data
	);

	ViewErrorHandler getErrorHandler();

}
