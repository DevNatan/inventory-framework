package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface ViewContext extends VirtualView {

    @NotNull
    ViewContextAttributes getAttributes();

    @NotNull
    List<Viewer> getViewers();

    <T> T get(@NotNull String key);

    <T> T get(@NotNull String key, @NotNull Supplier<T> defaultValue);

    void set(@NotNull String key, @NotNull Object value);

    boolean has(@NotNull String key);

    @NotNull
    ViewContainer getContainer();

    /** @deprecated Use {@link #getRoot()} instead. */
    @Deprecated
    default View getView() {
        return (View) getRoot();
    }

    AbstractView getRoot();

    String getUpdatedTitle();

    /**
     * Updates the title of the container for the client of the player who owns this context.
     *
     * <p>This should not be used before the container is opened, if you need to set the __initial
     * title__ use {@link OpenViewContext#setInventoryTitle(String)} on {@link
     * View#onOpen(OpenViewContext)} instead.
     *
     * <p>This function is not agnostic, so it may be that your server version is not yet supported,
     * if you try to use this function and fail (will possibly fail silently), report it to the
     * inventory-framework developers to add support to your version.
     *
     * @param title The new container title.
     */
    void updateTitle(@NotNull String title);

    /**
     * Updates the inventory title of the customer that owns this context to the initially defined
     * title. Must be used after {@link #updateTitle(String)} to take effect.
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
    void setPropagateErrors(boolean propagateErrors);

    boolean isCancelled();

    void setCancelled(boolean cancelled);

    // backward compatibility
    Player getPlayer();

    void open(@NotNull Class<? extends AbstractView> viewClass);

    void open(
            @NotNull Class<? extends AbstractView> viewClass,
            @NotNull Map<String, @Nullable Object> data);

    @ApiStatus.Internal
    ViewItem resolve(int index, boolean resolveOnRoot);

    ViewSlotContext ref(String key);

    /**
     * Converts this context to a pagination context.
     *
     * <p>It only works if the view that originated this context is a paginated view, throwing an
     * IllegalStateException if the root of this context is not paginated.
     *
     * @param <T> The pagination item type.
     * @return This context as a PaginatedViewContext.
     * @throws IllegalStateException If the root of this context is not paginated.
     */
    <T> PaginatedViewContext<T> paginated();
}
