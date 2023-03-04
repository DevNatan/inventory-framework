package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.state.StateHostAware;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import org.jetbrains.annotations.UnmodifiableView;

public interface IFContext extends VirtualView, StateHostAware {

    /**
     * An unique id for this context.
     * @return The unique identifier for this context.
     */
    @NotNull
    UUID getId();

    /**
     * The configuration for this context.
     * <p>
     * By default, contexts inherit their root configuration.
     *
     * @return The configuration for this context.
     */
    @NotNull
    ViewConfig getConfig();

    /**
     * An unmodifiable copy of all viewers that are tied to this context.
     *
     * @return All view of all viewers.
     */
    @NotNull
    @Unmodifiable
    Set<Viewer> getViewers();

    /**
     * An unmodifiable view of all viewers that are tied to this context.
     *
     * @return All unmodifiable view of all viewers.
     */
    @NotNull
    @UnmodifiableView
    Map<String, Viewer> getIndexedViewers();

    /**
     * Adds a new viewer to this context.
     *
     * @param viewer The viewer that'll be added.
     */
    void addViewer(@NotNull Viewer viewer);

    /**
     * Removes a new viewer to this context.
     *
     * @param viewer The viewer that'll be removed.
     */
    void removeViewer(@NotNull Viewer viewer);

    /**
     * The container of this context.
     * <p>
     * The container is where all the changes that are displayed to the user are applied.
     * <p>
     * Direct modifications to the container must launch an inventory modification error, which
     * signals that that function will change the container for whoever is seeing what, which, if it
     * is not possible at that moment or if the container is not sufficiently prepared for this,
     * it must fail.
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
    RootView getRoot();

    /**
     * The actual title of this context.
     * <p>
     * If the title has been dynamically changed, it will return the {@link #getUpdatedTitle() updated title}.
     *
     * @return The updated title, the current title of this view, if <code>null</code> will return
     * the default title for this view type.
     */
    @NotNull
    String getTitle();

    /**
     * The initial title of this context, that is, even if it has been changed, it will return the
     * title that has been initially defined.
     *
     * @return The initial title of this context, the current title of this view.
     */
    @NotNull
    String getInitialTitle();

    /**
     * Title that has been {@link #updateTitle(String) dynamically changed} in this context.
     *
     * @return The updated title or null if it wasn't updated.
     * @see #updateTitle(String)
     */
    @Nullable
    String getUpdatedTitle();

    /**
     * Updates the container title for everyone that's viewing it.
     *
     * <p>This should not be used before the container is opened, if you need to set the __initial
     * title__ use {@link IFOpenContext#setTitle(String)} on open handler instead.
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
     * Closes this context's container to all viewers who are viewing it.
     */
    void close();

    /**
     * Opens a new view for all viewers in that context.
     * <p>
     * This context will be immediately invalidated if there are no viewers left after opening.
     *
     * @param other The view to be opened.
     */
    void open(Class<? extends RootView> other);

    /**
     * All components in this context.
     *
     * @return An unmodifiable List view of all components in this context.
     */
    @NotNull
    @UnmodifiableView
    List<Component> getComponents();

    /**
     * Gets the component that is at a certain position.
     *
     * @param position The position.
     * @return The component in the given position or {@code null}.
     */
    Component getComponent(int position);

    /**
     * Adds a new component to this context.
     *
     * @param component The component to be added.
     */
    void addComponent(@NotNull Component component);

    /**
     * Removes a component from this context.
     *
     * @param component The component to be removed.
     */
    void removeComponent(@NotNull Component component);

    Pagination pagination();

    void updateRoot();

    boolean isMarkedForRemoval(int componentIndex);
}
