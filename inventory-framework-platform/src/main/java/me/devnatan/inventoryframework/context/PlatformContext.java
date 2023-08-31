package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PlatformContext extends AbstractIFContext {

    PlatformContext() {}

    @SuppressWarnings("rawtypes")
    @Override
    public abstract @NotNull PlatformView getRoot();

    /**
     * The actual title of this context.
     * <p>
     * If the title has been dynamically changed, it will return the {@link #getUpdatedTitle() updated title}.
     *
     * @return The updated title, the current title of this view, if <code>null</code> will return
     * the default title for this view type.
     */
    @NotNull
    public String getTitle() {
        return getUpdatedTitle() == null ? getInitialTitle() : getUpdatedTitle();
    }

    /**
     * Title that has been {@link #updateTitleForEveryone(String) dynamically changed} in this context.
     *
     * @return The updated title or null if it wasn't updated.
     * @see #updateTitleForEveryone(String)
     */
    @Nullable
    public final String getUpdatedTitle() {
        return getContainer().getTitle();
    }

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
    public final void updateTitleForEveryone(@NotNull String title) {
        for (final Viewer viewer : getViewers()) getContainer().changeTitle(title, viewer);
    }

    /**
     * Updates the container title to all viewers in this context, to the initially defined title.
     * Must be used after {@link #updateTitleForEveryone(String)} to take effect.
     */
    public final void resetTitleForEveryone() {
        for (final Viewer viewer : getViewers()) getContainer().changeTitle(null, viewer);
    }

    /**
     * Closes this context's container to all viewers who are viewing it.
     */
    public final void closeForEveryone() {
        getContainer().close();
    }

    /**
     * Opens a new view for all viewers in that context.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other The view to be opened.
     */
    public final void openForEveryone(Class<? extends RootView> other) {
        openForEveryone(other, null);
    }

    /**
     * Opens a new view for all viewers in that context with an initially defined data.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other       The view to be opened.
     * @param initialData The initial data.
     */
    @SuppressWarnings("unchecked")
    public final void openForEveryone(Class<? extends RootView> other, Object initialData) {
        getRoot().navigateTo(other, this, initialData);
    }
}
