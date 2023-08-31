package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;

/**
 * A confined context is a context derivation wholly subordinated to a parent context that
 * represents a closed scope of execution containing only one viewer, that is, when a player
 * interacts with an item and throws a click event a subordinate confined context with that player
 * as {@link Viewer} of a {@link IFSlotClickContext} is launched.
 */
public interface IFConfinedContext extends IFContext {

    /**
     * The viewer in current scope of execution.
     *
     * @return The {@link Viewer} in the current scope of execution.
     */
    Viewer getViewer();

    /**
     * Closes this context's container for the player in the current scope of execution.
     */
    void closeForPlayer();

    /**
     * Opens a new view only for the player that is in the current scope of execution.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other The view to be opened.
     */
    void openForPlayer(@NotNull Class<? extends RootView> other);

    /**
     * Opens a new view only for the player that is in the current scope of execution with an
     * initially defined data.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other The view to be opened.
     * @param initialData The initial context data.
     */
    void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData);

    /**
     * Updates the container title only for the player current scope of execution.
     *
     * <p>This should not be used before the container is opened, if you need to set the __initial
     * title__ use {@link IFOpenContext#modifyConfig()} on open handler instead.
     *
     * <p>This method is version dependant, so it may be that your server version is not yet
     * supported, if you try to use this method and fail (can fail silently), report it to the
     * library developers to add support to your version.
     *
     * @param title The new container title.
     */
    void updateTitleForPlayer(@NotNull String title);

    /**
     * Resets the container title only for the player current scope of execution to the initially
     * defined title. Must be used after {@link #updateTitleForPlayer(String)} to take effect.
     */
    void resetTitleForPlayer();
}
