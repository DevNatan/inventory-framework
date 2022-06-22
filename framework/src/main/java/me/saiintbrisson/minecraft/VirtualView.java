package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface VirtualView {

    @ApiStatus.Internal
    ViewItem getItem(int index);

    /**
     * The current title of this view's container.
     *
     * @return The title of container of this view.
     */
    String getTitle();

    /**
     * Returns the row count of this view.
     *
     * @return The row count of this view.compileOnly
     */
    int getRows();

    /** Mark this view to be closed when needed. */
    void close();

    /**
     * Closes this view immediately.
     *
     * @deprecated Use {@link #closeUninterruptedly()} instead.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
    void closeNow();

    /** Closes this view immediately. */
    void closeUninterruptedly();

    ViewErrorHandler getErrorHandler();

    /**
     * Registers a {@link ViewItem} in the specified slot.
     *
     * @param slot The item slot.
     */
    @NotNull
    ViewItem slot(int slot);

    /**
     * Registers a {@link ViewItem} with an item stack in the specified slot.
     *
     * @param slot The item slot.
     * @param item The item to be set.
     */
    @NotNull
    ViewItem slot(int slot, Object item);

    /**
     * Registers a {@link ViewItem} in the specified row and column.
     *
     * @param row The item slot row.
     * @param column The item slot column.
     */
    @NotNull
    ViewItem slot(int row, int column);

    /**
     * Registers a {@link ViewItem} with an item stack in the specified row and column.
     *
     * @param row The item slot row.
     * @param column The item slot column.
     * @param item The item to be set.
     */
    @NotNull
    ViewItem slot(int row, int column, Object item);

    void with(@NotNull ViewItem item);

    void update();

    @ApiStatus.Internal
    void clear(int slot);
}
