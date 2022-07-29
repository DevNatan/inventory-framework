package me.saiintbrisson.minecraft;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.function.Consumer;

public interface VirtualView {

	/**
	 * Finds an item at an index.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param index The item index.
	 * @return The item in the given index.
	 */
	@ApiStatus.Internal
	ViewItem getItem(int index);

	/**
	 * The current title of this view's container.
	 *
	 * @return The current title of this view's container.
	 */
	String getTitle();

	/**
	 * The row count of this view.
	 *
	 * @return The row count of this view.
	 */
	int getRows();

	/**
	 * The size of this view.
	 *
	 * @return The size of this view.
	 */
	int getSize();

	/**
	 * Mark this view to be closed.
	 *
	 * <p>Useful so that everything that should be executed is executed before the container is
	 * closed, thus invalidating all properties of a context for a view.
	 *
	 * <p>If you want the container to close immediately regardless of anything use {@link
	 * #closeUninterruptedly()} instead.
	 *
	 * @see #closeUninterruptedly()
	 */
	void close();

	/**
	 * Closes this view immediately.
	 *
	 * <p>Note that this function completely ignores any type of validation, that is, this function
	 * must be the last function called in the call stack, the container will be closed and any
	 * context tied to it will be invalidated, so any attempt to obtain or manipulate it of that
	 * context after that will fail.
	 *
	 * <p>Use the {@link #close()} variant to close the container after everything is properly
	 * handled.
	 *
	 * @see #close()
	 * @see #closeUninterruptedly()
	 * @deprecated Use {@link #closeUninterruptedly()} instead.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.2")
	void closeNow();

	/**
	 * Closes this view immediately.
	 *
	 * <p>Note that this function completely ignores any type of validation, that is, this function
	 * must be the last function called in the call stack, the container will be closed and any
	 * context tied to it will be invalidated, so any attempt to obtain or manipulate it of that
	 * context after that will fail.
	 *
	 * <p>Use the {@link #close()} variant to close the container after everything is properly
	 * handled.
	 *
	 * @see #close()
	 */
	void closeUninterruptedly();

	/**
	 * The error handler for this view.
	 *
	 * @return The error handler for this view or null if it was not defined.
	 */
	@Nullable
	ViewErrorHandler getErrorHandler();

	/**
	 * Defines the error handler for this view.
	 *
	 * <p>Setting specific error handling for a {@link ViewContext} will cause the error to be
	 * propagated to the {@link View} as well if it has been set.
	 *
	 * @param errorHandler The error handler for this view. Use null to remove it.
	 */
	void setErrorHandler(@Nullable ViewErrorHandler errorHandler);

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
	 * Registers a new item in the specified slot.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @param slot The item slot.
	 * @return The newly created item instance.
	 */
	@NotNull
	ViewItem slot(int slot);

	/**
	 * Registers a new item with a fallback item in the specified slot.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @param slot The item slot.
	 * @param item The fallback item.
	 * @return The newly created item instance.
	 */
	@NotNull
	ViewItem slot(int slot, Object item);

	/**
	 * Registers a new item in the specified row and column.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @param row    The item slot row.
	 * @param column The item slot column.
	 * @return The newly created item instance.
	 */
	@NotNull
	ViewItem slot(int row, int column);

	/**
	 * Registers a new item with an fallback item in the specified row and column.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @param row    The slot row.
	 * @param column The slot column.
	 * @param item   The fallback item.
	 * @return The newly created item instance.
	 */
	@NotNull
	ViewItem slot(int row, int column, Object item);

	/**
	 * Registers a new item in the first slot of this view.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @return The newly created item instance.
	 * @see #getFirstSlot()
	 */
	@NotNull
	ViewItem firstSlot();

	/**
	 * Registers a new item with a fallback item in the first slot of this view.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @param item The fallback item.
	 * @return The newly created item instance.
	 * @see #getFirstSlot()
	 */
	@NotNull
	ViewItem firstSlot(Object item);

	/**
	 * Registers a new item in the last slot of this view.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @return The newly created item instance.
	 * @see #getLastSlot()
	 */
	@NotNull
	ViewItem lastSlot();

	/**
	 * Registers a new item with a fallback item in the last slot of this view.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 *
	 * @param item The fallback item.
	 * @return The newly created item instance.
	 * @see #getLastSlot()
	 */
	@NotNull
	ViewItem lastSlot(Object item);

	/**
	 * Registers a new slot in the next available slot of this view.
	 * <p>
	 * Implementation details:
	 * <ul>
	 *     <li>Items of similar types that can possibly be stacked will not be stacked.</li>
	 *     <li>Items in an dummy state (no fallback item or rendering function) will be treated as
	 *     filled, and skipped.</li>
	 *     <li>In layered views, layouts that have {@link PaginatedVirtualView#setLayout(char, Consumer) user-defined characters}
	 *     or that are <a href="https://github.com/DevNatan/inventory-framework/wiki/Pagination#layout-reserved-navigation-characters">reserved characters</a>
	 *     that are not "empty" will be treated as filled, and skipped.
	 *     </li>
	 *     <li>In paginated views the available slots will go from {@link PaginatedView#getOffset()}
	 *     to {@link PaginatedView#getLimit()}.</li>
	 *     <li>In paginated & layered views will respect layout boundaries.</li>
	 *     <li>Result slots depending on the {@link AbstractView#getType() view type} will be skipped
	 *     if they are not {@link ViewType#canPlayerInteractOn(int) interactable}.</li>
	 * </ul>
	 * <p>
	 * In regular views whose item is not dynamically defined, the slot the item will belong to can
	 * be obtained from the returned item instance. Dynamic items will have their starting slot set
	 * to {@link ViewItem#AVAILABLE_SLOT}.
	 * <p>
	 * The definition of new slots using this method in a non-dynamic way, that is, defining these
	 * slots in the constructor in non-regular views will be treated as regular slots respecting the
	 * rules of a regular normal view and ignoring the contractions of these views if their nature
	 * is mostly dynamic.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @return The newly created item instance.
	 */
	@ApiStatus.Experimental
	@NotNull
	ViewItem availableSlot();

	/**
	 * Registers a new slot with a fallback item in the next available slot of this view.
	 * <p>
	 * Implementation details:
	 * <ul>
	 *     <li>Items of similar types that can possibly be stacked will not be stacked.</li>
	 *     <li>Items in an dummy state (no fallback item or rendering function) will be treated as
	 *     filled, and skipped.</li>
	 *     <li>In layered views, layouts that have {@link PaginatedVirtualView#setLayout(char, Consumer) user-defined characters}
	 *     or that are <a href="https://github.com/DevNatan/inventory-framework/wiki/Pagination#layout-reserved-navigation-characters">reserved characters</a>
	 *     that are not "empty" will be treated as filled, and skipped.
	 *     </li>
	 *     <li>In paginated views the available slots will go from {@link PaginatedView#getOffset()}
	 *     to {@link PaginatedView#getLimit()}.</li>
	 *     <li>In paginated & layered views will respect layout boundaries.</li>
	 *     <li>Result slots depending on the {@link AbstractView#getType() view type} will be skipped
	 *     if they are not {@link ViewType#canPlayerInteractOn(int) interactable}.</li>
	 * </ul>
	 * <p>
	 * In regular views whose item is not dynamically defined, the slot the item will belong to can
	 * be obtained from the returned item instance. Dynamic items will have their starting slot set
	 * to {@link ViewItem#AVAILABLE_SLOT}.
	 * <p>
	 * The definition of new slots using this method in a non-dynamic way, that is, defining these
	 * slots in the constructor in non-regular views will be treated as regular slots respecting the
	 * rules of a regular normal view and ignoring the contractions of these views if their nature
	 * is mostly dynamic.
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @param item The fallback item.
	 * @return The newly created item instance.
	 */
	@ApiStatus.Experimental
	@NotNull
	ViewItem availableSlot(Object item);

	/**
	 * Creates a new item instance.
	 *
	 * <p>This is just here for backwards compatibility.
	 *
	 * @return The newly created item instance.
	 * @deprecated Mutable item instances will be provided and should no longer be created.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	ViewItem item();

	/**
	 * Creates a new item instance with a fallback item.
	 *
	 * <p>This is just here for backwards compatibility.
	 *
	 * @param item The fallback item.
	 * @return The newly created item instance.
	 * @deprecated Mutable item instances will be provided and should no longer be created.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	ViewItem item(@NotNull ItemStack item);

	/**
	 * Creates a new item instance with a fallback item.
	 *
	 * <p>This is just here for backwards compatibility.
	 *
	 * @param material The fallback item material.
	 * @return The newly created item instance.
	 * @deprecated Mutable item instances will be provided and should no longer be created.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	ViewItem item(@NotNull Material material);

	/**
	 * Creates a new item instance with a fallback item.
	 *
	 * <p>This is just here for backwards compatibility.
	 *
	 * @param material   The fallback item material.
	 * @param durability The fallback item durability.
	 * @return The newly created item instance.
	 * @deprecated Mutable item instances will be provided and should no longer be created.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	ViewItem item(@NotNull Material material, short durability);

	/**
	 * Creates a new item instance with a fallback item.
	 *
	 * <p>This is just here for backwards compatibility.
	 *
	 * @param material The fallback item material.
	 * @param amount   The fallback item amount.
	 * @return The newly created item instance.
	 * @deprecated Mutable item instances will be provided and should no longer be created.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	ViewItem item(@NotNull Material material, int amount);

	/**
	 * Creates a new item instance with a fallback item.
	 *
	 * <p>This is just here for backwards compatibility.
	 *
	 * @param material   The fallback item material.
	 * @param amount     The fallback item amount.
	 * @param durability The fallback item durability.
	 * @return The newly created item instance.
	 * @deprecated Mutable item instances will be provided and should no longer be created.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.3")
	ViewItem item(@NotNull Material material, int amount, short durability);

	/**
	 * Updates this view.
	 *
	 * <p><b>Triggers an {@link #inventoryModificationTriggered() inventory modification}.</b>
	 */
	void update();

	/**
	 * Clears an item at the specified position.
	 *
	 * @param index The item index.
	 */
	void clear(int index);

	/**
	 * The update job for this view.
	 *
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 *
	 * @return The update job for this view.
	 */
	@ApiStatus.Internal
	ViewUpdateJob getUpdateJob();

	/**
	 * Sets the update job for this view.
	 *
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 *
	 * @param updateJob The new update job.
	 */
	@ApiStatus.Internal
	void setUpdateJob(ViewUpdateJob updateJob);

	/**
	 * Defines the automatic update interval time for this view.
	 *
	 * @param intervalInTicks The (interval in ticks) to wait between updates.
	 */
	void scheduleUpdate(long intervalInTicks);

	/**
	 * Defines the automatic update interval time for this view.
	 *
	 * @param delayInTicks    The delay (in ticks) to wait before running the task.
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

	/**
	 * Thrown when a method explicitly needs to specify that it will directly modify the view's
	 * container when executed, that method is overridden by implementations whose direct modification
	 * of the container is not allowed, throwing an exception.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @throws IllegalStateException If a direct modification to the container is not allowed.
	 */
	@ApiStatus.Internal
	void inventoryModificationTriggered();
}
