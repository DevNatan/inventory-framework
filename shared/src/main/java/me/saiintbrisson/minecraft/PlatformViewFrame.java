package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.feature.FeatureInstaller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

@ApiStatus.NonExtendable
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
	 * @return This platform view frame.
	 * @throws IllegalStateException If this view frame is already registered.
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

	/**
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of this
	 * library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The current component factory.
	 * @deprecated Use {@link PlatformUtils#getFactory()} instead.
	 */
	@Deprecated
	@ApiStatus.Internal
	@NotNull
	ViewComponentFactory getFactory();

	/**
	 * Opens the given view to a viewer.
	 * <p>
	 * <b><i> This is an internal inventory-framework API that should not be used from outside of this
	 * library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param viewClass The view class type.
	 * @param viewer    Who the view will be open.
	 * @param <T>       The view instance type.
	 * @return The opened view instance.
	 */
	@NotNull <T extends AbstractView> T open(@NotNull Class<T> viewClass, @NotNull V viewer);

	/**
	 * Opens the given view to a viewer with initial data.
	 * <p>
	 * <b><i> This is an internal inventory-framework API that should not be used from outside of this
	 * library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param viewClass The view class type.
	 * @param viewer    Who the view will be open.
	 * @param data      Initial viewer context data
	 * @param <T>       The view instance type.
	 * @return The opened view instance.
	 */
	@NotNull <T extends AbstractView> T open(@NotNull Class<T> viewClass, @NotNull V viewer, Map<String, Object> data);

	/**
	 * Opens the given view to a viewer with initial data.
	 * <p>
	 * <b><i> This is an internal inventory-framework API that should not be used from outside of this
	 * library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param viewClass The view class type.
	 * @param viewer    Who the view will be open.
	 * @param data      Initial viewer context data
	 * @param <T>       The view instance type.
	 * @return The opened view instance.
	 */
	@ApiStatus.Internal
	@NotNull
	default <T extends AbstractView> T open(
		@NotNull Class<T> viewClass, @NotNull Viewer viewer, Map<String, Object> data) {
		throw new UnsupportedOperationException(
			"Direct viewer opening is not supported by this ViewFrame implementation.");
	}

	/**
	 * Defines what will be the default backward navigation item for all registered views.
	 * <p>
	 * If the view has a navigation item defined, it will use the navigation item itself, either its
	 * own or from the context, with the fallback being the item that will be defined from here.
	 *
	 * <pre><code>
	 *     withPreviousPageItem(($, item) -> item.withItem(...))
	 * </code></pre>
	 * <p>
	 * You can set individual navigation items for {@link AbstractPaginatedView paginated views}
	 * with {@link PaginatedVirtualView#setPreviousPageItem(BiConsumer)}.
	 *
	 * @param previousPageItemFactory The navigation item factory.
	 * @return This platform view frame.
	 */
	F withPreviousPageItem(@Nullable BiConsumer<PaginatedViewContext<?>, ViewItem> previousPageItemFactory);

	/**
	 * Defines what will be the default forward navigation item for all registered views.
	 * <p>
	 * If the view has a navigation item defined, it will use the navigation item itself, either its
	 * own or from the context, with the fallback being the item that will be defined from here.
	 *
	 * <pre><code>
	 *     withNextPageItem(($, item) -> item.withItem(...))
	 * </code></pre>
	 * <p>
	 * You can set individual navigation items for {@link AbstractPaginatedView paginated views}
	 * with {@link PaginatedVirtualView#setNextPageItem(BiConsumer)}.
	 *
	 * @param nextPageItemFactory The navigation item factory.
	 * @return This platform view frame.
	 */
	F withNextPageItem(@Nullable BiConsumer<PaginatedViewContext<?>, ViewItem> nextPageItemFactory);

	@ApiStatus.Internal
	ViewErrorHandler getErrorHandler();

	/**
	 * @deprecated Use {@link #withErrorHandler(ViewErrorHandler)} instead.
	 */
	@Deprecated
	void setErrorHandler(ViewErrorHandler errorHandler);

	/**
	 * Defines the global error handler.
	 * <p>
	 * This error handler is global, called whenever an error occurs in the lifecycle of a view
	 * registered in that view frame.
	 * <p>
	 * Error handlers can be set per view or per context, and it is possible to optionally
	 * {@link ViewContext#setPropagateErrors(boolean) propagate errors} from the context to the view,
	 * but even if this option is disabled errors will be propagated to the global error handler.
	 *
	 * <pre><code>
	 *     class MyErrorHandler implements ViewErrorHandler {
	 *
	 *         &#64;Override
	 *         public void error(ViewContext context, Exception exception) {
	 *             ...
	 *         }
	 *
	 *     }
	 *
	 * viewFrame.withErrorHandler(new MyErrorHandler());
	 * </code></pre>
	 * <p>
	 * {@link ViewErrorHandler} is a <code>@FunctionalInterface</code> so you can use lambda.
	 * <pre><code>
	 *     viewFrame.withErrorHandler((context, exception) -> { ... });
	 * </code></pre>
	 *
	 * @param errorHandler The global error handler.
	 * @return This platform view frame.
	 */
	F withErrorHandler(@NotNull ViewErrorHandler errorHandler);

	/**
	 * Executes a function on the next iteration.
	 * <p>
	 * The implementation of this function is per platform and should essentially be used to ensure
	 * synchronization guarantee across IF methods.
	 *
	 * @param runnable The task that'll be run.
	 */
	void nextTick(@NotNull Runnable runnable);

	/**
	 * The factory for the navigation backwards item.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The factory for the navigation backwards item.
	 * @see #withPreviousPageItem(BiConsumer)
	 */
	@ApiStatus.Internal
	BiConsumer<PaginatedViewContext<?>, ViewItem> getPreviousPageItem();

	/**
	 * The factory for the navigation advance item.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The factory for the navigation advance item.
	 * @see #withNextPageItem(BiConsumer)
	 */
	@ApiStatus.Internal
	BiConsumer<PaginatedViewContext<?>, ViewItem> getNextPageItem();

	/**
	 * @deprecated Use {@link #getPreviousPageItem()} instead.
	 */
	@Deprecated
	Function<PaginatedViewContext<?>, ViewItem> getDefaultPreviousPageItem();

	/**
	 * @deprecated Use {@link #getNextPageItem()} instead.
	 */
	@Deprecated
	Function<PaginatedViewContext<?>, ViewItem> getDefaultNextPageItem();

	/**
	 * @deprecated Use {@link #withPreviousPageItem(BiConsumer)} instead.
	 */
	@Deprecated
	void setDefaultPreviousPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItemFactory);


	/**
	 * @deprecated Use {@link #withNextPageItem(BiConsumer)} (BiConsumer)} instead.
	 */
	@Deprecated
	void setDefaultNextPageItem(Function<PaginatedViewContext<?>, ViewItem> defaultNextPageItemFactory);

	/**
	 * @deprecated Use {@link #withPreviousPageItem(BiConsumer)} instead.
	 */
	@Deprecated
	F setNavigateBackItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateBackItemFactory);

	/**
	 * @deprecated Use {@link #withNextPageItem(BiConsumer)} instead.
	 */
	@Deprecated
	F setNavigateNextItemFactory(BiConsumer<PaginatedViewContext<?>, ViewItem> navigateNextItemFactory);
}