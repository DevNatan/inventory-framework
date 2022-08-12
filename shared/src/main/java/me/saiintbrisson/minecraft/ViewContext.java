package me.saiintbrisson.minecraft;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.jetbrains.annotations.UnmodifiableView;

public interface ViewContext extends VirtualView {

    @ApiStatus.Internal
    Map<String, Object> getData();

    /**
     * An unmodifiable collection of all viewers that are tied to this context.
     *
     * @return All unmodifiable collection of all viewers.
     */
    @NotNull
    @UnmodifiableView
    List<Viewer> getViewers();

	@ApiStatus.Internal
	void addViewer(@NotNull Viewer viewer);

    /**
     * The container of this context.
     *
     * <p>The container is where all the changes that are displayed to the user are applied.
     *
     * <p>Direct modifications to the container must launch an {@link
     * AbstractVirtualView#inventoryModificationTriggered()}, which signals that that function will
     * change the container for whoever is seeing what, which, if it is not possible at that moment or
     * if the container is not sufficiently prepared for this, it must fail. .
     *
     * @return The container of this context.
     */
    @NotNull
    ViewContainer getContainer();

    /**
     * View root from which this context originated.
     *
     * @return The root of this context.
     */
    @NotNull
    AbstractView getRoot();

    /**
     * This is just here for backwards compatibility.
     *
     * @return The root of this context.
     * @see #getRoot()
     * @deprecated Use {@link #getRoot()} instead.
     */
    @Deprecated
    default View getView() {
        return (View) getRoot();
    }

    /**
     * {@inheritDoc}
     *
     * <p>If the title has been dynamically changed, it will return {@link #getUpdatedTitle()}.
     *
     * @return The updated title, the current title of this view, if <code>null</code> will return
     * the default title for this view type.
     */
    @Override
    String getTitle();

    /**
     * Returns the initial title of this context, that is, even if it has been changed,
     * it will return the initial title.
     *
     * @return The initial title of this context, the current title of this view, if <code>null</code>
     * will return the default title for this view type.
     */
    @Nullable
    String getInitialTitle();

    /**
     * The title dynamically updated in this context.
     *
     * @return The title dynamically updated in this context or null if it wasn't updated.
     * @see #updateTitle(String)
     */
    @Nullable
    String getUpdatedTitle();

    /**
     * Updates the container title for everyone that's viewing it.
     *
     * <p>This should not be used before the container is opened, if you need to set the __initial
     * title__ use {@link OpenViewContext#setContainerTitle(String)} on {@link
     * View#onOpen(OpenViewContext)} instead.
     *
     * <p>This method is version dependant, so it may be that your server version is not yet
     * supported, if you try to use this method and fail (can fail silently), report it to the library
     * developers to add support to your version.
     *
     * @param title The new container title.
     */
    void updateTitle(@NotNull String title);

    /**
     * Updates the container title to all viewers in this context, to the initially defined title.
     * Must be used after {@link #updateTitle(String)} to take effect.
     */
    void resetTitle();

    /**
     * If errors should be propagated to the view's error handler for this context. Always <code>
     * true</code> by default.
     *
     * @return If errors will be propagated to the View.
     */
    boolean isPropagateErrors();

    /**
     * Defines whether errors should be propagated to the root view's error handler.
     *
     * @param propagateErrors If errors should be propagated to the root view.
     */
    void setPropagateErrors(boolean propagateErrors);

    @ApiStatus.Internal
    boolean isMarkedToClose();

    @ApiStatus.Internal
    void setMarkedToClose(boolean markedToClose);

    /**
     * Checks if this context is cancelled.
     *
     * @return If this context is cancelled.
     */
    boolean isCancelled();

    /**
     * Cancels this context.
     *
     * @param cancelled The new cancellation state.
     * @throws IllegalStateException If this context is not cancellable.
     */
    void setCancelled(boolean cancelled);

    /**
     * Gets a value of a property defined for this context for a specific key.
     *
     * @param key The property key.
     * @param <T> The property value type.
     * @return The property value for the given key.
     * @see #get(String, Supplier)
     */
    @UnknownNullability("Value can be null if the user provides a null property value")
    <T> T get(@NotNull String key);

    /**
     * Gets a value of a property defined for this context for a specific key falling back to a
     * default value if the key is not returned.
     *
     * @param key          The property key.
     * @param defaultValue The value that will be returned if the property is not found.
     * @param <T>          The property value type.
     * @return The property value for the given key.
     * @see #get(String)
     */
    @UnknownNullability("Value can be null if the user provides a null property value")
    <T> T get(@NotNull String key, @NotNull Supplier<T> defaultValue);

    /**
     * Defines a property in this context.
     *
     * @param key   The property key.
     * @param value The property value.
     */
    void set(@NotNull String key, @NotNull Object value);

    /**
     * If a property with a specified key has been defined for this context.
     *
     * @param key The property key.
     * @return If property was defined.
     */
    boolean has(@NotNull String key);

    /**
     * Converts this context to a paginated context.
     *
     * <p>Only works if the view that originated this context is a paginated view, throwing an
     * IllegalStateException if the {@link #getRoot() root} of this context is not paginated.
     *
     * @param <T> The pagination item type.
     * @return This context as a PaginatedViewContext.
     * @throws IllegalStateException If the root of this context cannot be converted to a paginated.
     */
	@Override
    <T> PaginatedViewContext<T> paginated();

    /**
     * The player tied to this context.
     *
     * <p>This is just here for backwards compatibility.
     *
     * @return The player tied to this context.
     */
    @NotNull
    Player getPlayer();

    void open(@NotNull Class<? extends AbstractView> viewClass);

    void open(@NotNull Class<? extends AbstractView> viewClass, @NotNull Map<String, @Nullable Object> data);

    /**
     * Creates a new slot context instance containing within it data of an item whose reference key is
     * the same as specified.
     *
     * <p>Item reference keys are used as a bridge between one item and another, ideal when there are
     * items that interact with each other, for example: you click on an item and another item in the
     * container is updated.
     *
     * <p>In previous versions this was not possible as only updating the entire container was it
     * possible to update an item to which the modification was applied causing side effects on other
     * items that had rendering functions that act during the update, or, paginated views which items
     * and layout were resolved again, so the performance using references is much better than a full
     * container update.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param key The item reference key.
     * @return A new context with referenced item.
     * @throws IllegalArgumentException If no item are found for the reference key.
     * @see ViewItem#referencedBy(String)
     */
    @ApiStatus.Experimental
    @NotNull
    ViewSlotContext ref(String key) throws IllegalArgumentException;
}
