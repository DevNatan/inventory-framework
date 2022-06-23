package me.saiintbrisson.minecraft;

import java.time.Duration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
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

    /**
     * Gets the error handler for this view.
     *
     * @return The ViewErrorHandler for this view.
     */
    ViewErrorHandler getErrorHandler();

    /**
     * Defines the error handler for this view.
     *
     * <p>Setting specific error handling for a {@link ViewContext} will cause the error to be
     * propagated to the {@link View} as well if it has been set.
     *
     * @param errorHandler The View Error Handler for this view. Use <code>null</code> to remove it.
     */
    void setErrorHandler(ViewErrorHandler errorHandler);

    /**
     * The first slot of this view.
     *
     * @return The position of the first slot of this view.
     */
    int getFirstSlot();

    /**
     * The last slot of this view.
     *
     * @return The position of the last slot of this view.
     */
    int getLastSlot();

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

    @NotNull
    ViewItem firstSlot();

    @NotNull
    ViewItem firstSlot(Object item);

    @NotNull
    ViewItem lastSlot();

    @NotNull
    ViewItem lastSlot(Object item);

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    ViewItem item();

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    ViewItem item(@NotNull ItemStack item);

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    ViewItem item(@NotNull Material material);

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    ViewItem item(@NotNull Material material, short durability);

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    ViewItem item(@NotNull Material material, int amount);

    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
    ViewItem item(@NotNull Material material, int amount, short durability);

    @ApiStatus.Experimental
    void with(@NotNull ViewItem item);

    void update();

    void clear(int slot);

    @ApiStatus.Internal
    ViewUpdateJob getUpdateJob();

    @ApiStatus.Internal
    void setUpdateJob(ViewUpdateJob job);

    /**
     * Defines the automatic update interval time for this view.
     *
     * @param intervalInTicks The (interval in ticks) to wait between updates.
     */
    void scheduleUpdate(long intervalInTicks);

    /**
     * Defines the automatic update interval time for this view.
     *
     * @param delayInTicks The delay (in ticks) to wait before running the task.
     * @param intervalInTicks The interval (in ticks) to wait between updates.
     */
    void scheduleUpdate(long delayInTicks, long intervalInTicks);

    /**
     * Defines the automatic update interval time for this view.
     *
     * @param duration The duration to wait between updates.
     */
    void scheduleUpdate(@NotNull Duration duration);

    /**
     * Checks if this view is set to update automatically.
     *
     * @return <code>true</code> if it will update automatically or <code>false</code> otherwise.
     */
    boolean isScheduledToUpdate();
}
