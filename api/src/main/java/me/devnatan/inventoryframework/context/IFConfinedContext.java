package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;

public interface IFConfinedContext extends IFContext {

    @NotNull
    Viewer getViewer();

    /**
     * Closes this context's container for the player in the current scope of execution.
     */
    void closeForPlayer();

    /**
     * Opens a new view only for the player that is in the execution context of that context.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other The view to be opened.
     */
    void openForPlayer(Class<? extends RootView> other);

    /**
     * Updates the container title for everyone that's viewing it.
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
     * Updates the container title to all viewers in this context, to the initially defined title.
     * Must be used after {@link #updateTitleForPlayer(String)} to take effect.
     */
    void resetTitleForPlayer();
}
