package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.exception.InventoryModificationException;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Deque;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * VirtualView is the basis for creating a view it contains the implementation with methods that are
 * shared between regular views and contexts which are called "unified methods".
 * <p>
 * We call "view" a {@link VirtualView}, "regular view" a {@link AbstractView} and implementations,
 * and "context" a {@link ViewContext} and implementations.
 */
public interface VirtualView {

	/**
	 * Character used to represent the paginated view "previous page" navigation slot on a layout.
	 */
	@ApiStatus.Internal
	char LAYOUT_PREVIOUS_PAGE = '<';

	/**
	 * Character used to represent the paginated view "next page" navigation slot on a layout.
	 */
	@ApiStatus.Internal
	char LAYOUT_NEXT_PAGE = '>';

	/**
	 * Character used to represent a empty slot on a layout.
	 */
	@ApiStatus.Internal
	char LAYOUT_EMPTY_SLOT = 'X';

	/**
	 * Character used to represent a filled slot on a layout.
	 */
	@ApiStatus.Internal
	char LAYOUT_FILLED_SLOT = 'O';

	@ApiStatus.Internal
	ViewItem[] getItems();

	@ApiStatus.Internal
	void setItems(ViewItem[] items);

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
	 * The current title of this view.
	 *
	 * @return The current title of this view, if <code>null</code> will return the default title
	 * for this view type.
	 */
	@Nullable
	String getTitle();

	/**
	 * The rows count of this view.
	 *
	 * @return The rows count of this view.
	 */
	int getRows();

	/**
	 * The columns count of this view.
	 *
	 * @return The columns count of this view.
	 */
	int getColumns();

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
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
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
	 * Registers a new item with a fallback item in the specified row and column.
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
	 * On layered regular views (layout + AbstractView), layouts that have {@link PaginatedVirtualView#setLayout(char, Consumer) user-defined characters}
	 * or that are <a href="https://github.com/DevNatan/inventory-framework/wiki/Pagination#layout-reserved-navigation-characters">non-item reserved characters</a>
	 * will be skipped.
	 * <pre><code>
	 * class MyView extends View {
	 *
	 *     MyView() {
	 *         setLayout("XZZZOOOOX");
	 *         setLayout('Z', item -&#60; ...)
	 *
	 *         // will go to the layout's first available fillable ('O') slot after 'Z'
	 *         availableSlot(...);
	 *
	 *         // not available since layered views slots are defined on layout resolution
	 *         int slot = availableSlot(...).getSlot();
	 *     }
	 * }
	 * </code></pre>
	 * <p>
	 * If the root view has a layout and the context does not have a layout but this function is
	 * used, the available slot will be the first slot after the slots rendered from the root view
	 * preserving the insertion order, in which case the root view has priority.
	 * <pre><code>
	 * class MyView extends View {
	 *
	 *     MyView() {
	 *         setLayout("XOOOOOOOOX");
	 *         availableSlot(...); // 1
	 *     }
	 *
	 *     &#64;Override
	 *     protected void onRender(ViewContext context) {
	 *         context.availableSlot(...); // 2
	 *     }
	 *
	 *     &#64;Override
	 *     protected void onUpdate(ViewContext context) {
	 *         context.availableSlot(...); // DON'T!! Ignored on update context
	 *     }
	 *
	 *     &#64;Override
	 *     protected void onClick(ViewSlotClickContext context) {
	 *         context.availableSlot(...); // DON'T!! Prohibited on slot context
	 *     }
	 *
	 * }
	 * </code></pre>
	 *
	 * <p>
	 * In regular views whose item is not dynamically defined, the slot the item will belong to can
	 * be obtained from the returned item instance. Dynamic items will have their starting slot set
	 * to {@link ViewItem#AVAILABLE}. Use item render function to get around this and get
	 * always the correct slot that item is placed on.
	 * <pre><code>
	 * availableSlot(...).onRender(render -&#60; {
	 *     int slot = render.getSlot();
	 * });
	 * </code></pre>
	 * <p>
	 * The definition of new slots using this method in a non-dynamic way, that is, defining these
	 * slots in the constructor in non-regular views will be treated as regular slots respecting the
	 * rules of a regular view and ignoring the contractions of these views if their nature
	 * is mostly dynamic.
	 * <p>
	 * <b>Not supported on paginated views.</b>
	 * <ul>
	 *     <li>Items of similar types that can possibly be stacked will not be stacked.</li>
	 *     <li>
	 *         Result slots depending on the {@link AbstractView#getType() view type} will be skipped
	 *         if they are not {@link ViewType#canPlayerInteractOn(int) interactable}.
	 *     </li>
	 * </ul>
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
	 * On layered regular views (layout + AbstractView), layouts that have {@link PaginatedVirtualView#setLayout(char, Consumer) user-defined characters}
	 * or that are <a href="https://github.com/DevNatan/inventory-framework/wiki/Pagination#layout-reserved-navigation-characters">non-item reserved characters</a>
	 * will be skipped.
	 * <pre><code>
	 * class MyView extends View {
	 *
	 *     MyView() {
	 *         setLayout("XZZZOOOOX");
	 *         setLayout('Z', item -&#60; ...)
	 *
	 *         // will go to the layout's first available fillable ('O') slot after 'Z'
	 *         availableSlot(...);
	 *
	 *         // not available since layered views slots are defined on layout resolution
	 *         int slot = availableSlot(...).getSlot();
	 *     }
	 * }
	 * </code></pre>
	 * <p>
	 * If the root view has a layout and the context does not have a layout but this function is
	 * used, the available slot will be the first slot after the slots rendered from the root view
	 * preserving the insertion order, in which case the root view has priority.
	 * <pre><code>
	 * class MyView extends View {
	 *
	 *     MyView() {
	 *         setLayout("XOOOOOOOOX");
	 *         availableSlot(...); // 1
	 *     }
	 *
	 *     &#64;Override
	 *     protected void onRender(ViewContext context) {
	 *         context.availableSlot(...); // 2
	 *     }
	 *
	 *     &#64;Override
	 *     protected void onUpdate(ViewContext context) {
	 *         context.availableSlot(...); // DON'T!! Ignored on update context
	 *     }
	 *
	 *     &#64;Override
	 *     protected void onClick(ViewSlotClickContext context) {
	 *         context.availableSlot(...); // DON'T!! Prohibited on slot context
	 *     }
	 *
	 * }
	 * </code></pre>
	 *
	 * <p>
	 * In regular views whose item is not dynamically defined, the slot the item will belong to can
	 * be obtained from the returned item instance. Dynamic items will have their starting slot set
	 * to {@link ViewItem#AVAILABLE}. Use item render function to get around this and get
	 * always the correct slot that item is placed on.
	 * <pre><code>
	 * availableSlot(...).onRender(render -&#60; {
	 *     int slot = render.getSlot();
	 * });
	 * </code></pre>
	 * <p>
	 * The definition of new slots using this method in a non-dynamic way, that is, defining these
	 * slots in the constructor in non-regular views will be treated as regular slots respecting the
	 * rules of a regular view and ignoring the contractions of these views if their nature
	 * is mostly dynamic.
	 * <p>
	 * <b>Not supported on paginated views.</b>
	 * <ul>
	 *     <li>Items of similar types that can possibly be stacked will not be stacked.</li>
	 *     <li>
	 *         Result slots depending on the {@link AbstractView#getType() view type} will be skipped
	 *         if they are not {@link ViewType#canPlayerInteractOn(int) interactable}.
	 *     </li>
	 * </ul>
	 *
	 * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
	 * such API may be changed or may be removed completely in any further release. </i></b>
	 *
	 * @param fallbackItem The fallback item.
	 * @return The newly created item instance.
	 */
	@ApiStatus.Experimental
	@NotNull
	ViewItem availableSlot(Object fallbackItem);

	/**
	 * Creates a new item instance.
	 *
	 * <p>This is just here for backwards compatibility.
	 *
	 * @return The newly created item instance.
	 * @deprecated Mutable item instances will be provided and should no longer be created.
	 */
	@Deprecated
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
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
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
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
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
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
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
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
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
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
	@ApiStatus.ScheduledForRemoval(inVersion = "2.5.5")
	ViewItem item(@NotNull Material material, int amount, short durability);

	/**
	 * Registers the given item in the specified slot.
	 *
	 * @param item The item.
	 * @param slot The slot.
	 */
	@ApiStatus.Internal
	void apply(@Nullable ViewItem item, int slot);

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
	 * @throws InventoryModificationException If a direct modification to the container is not allowed.
	 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Errors#inventorymodificationexception">InventoryModificationException on Wiki</a>
	 */
	@ApiStatus.Internal
	void inventoryModificationTriggered() throws InventoryModificationException;

	/**
	 * The layout defined for this view by the user.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The layout defined for this view.
	 */
	@ApiStatus.Internal
	@Nullable
	String[] getLayout();

	/**
	 * All layout patterns set by {@link #setLayout(char, Consumer)}.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return An unmodifiable view of all layout patterns available.
	 */
	@ApiStatus.Internal
	List<LayoutPattern> getLayoutPatterns();

	/**
	 * The layout is a pattern of characters that you will use to determine where each item will go
	 * on your view and is used to replace the tiring work of determining item slots manually,
	 * it works for paginated views or regular views that use {@link #availableSlot() auto-slot-setting}.
	 * <p>
	 * You can use the following characters on a layout:
	 * <ul>
	 *     <li>{@link #LAYOUT_FILLED_SLOT O} an filled slot in the view.</li>
	 *     <li>{@link #LAYOUT_EMPTY_SLOT X} an empty slot in the view.</li>
	 *     <li>{@link #LAYOUT_PREVIOUS_PAGE &#60;} where "previous page" item for {@link PaginatedVirtualView paginated views} will be positioned</li>
	 *     <li>{@link #LAYOUT_NEXT_PAGE &#62;} where "next page" item for {@link PaginatedVirtualView paginated views} will be positioned</li>
	 * </ul>
	 * <b>You can define layouts in two scopes:</b>
	 * <ul>
	 *     <li>{@link AbstractView regular view}: The same layout will be used in that view forever.</li>
	 *     <li>{@link ViewContext context}: Only a specific context will use a layout pattern which for some reason
	 *     must be different from the layout defined in the View or other layouts.</li>
	 * </ul>
	 * <p>
	 * <b>Note:</b> If you define both layouts, for the regular view and for the context, the layout of
	 * the context will take precedence.
	 * <p>
	 * <b>There are rules for creating a layout that you should pay attention to:</b>
	 * <ul>
	 *     <li>The width of the layout must be the same number of columns as the view.</li>
	 *     <li>The height of the layout must be the same number of rows as the view.</li>
	 *     <li>If you define an item in the layout and don't define it in the code an error will be thrown.</li>
	 * </ul>
	 * <p>
	 *
	 * @param layout The layout.
	 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Pagination#layout">Layouts on Wiki</a>
	 */
	void setLayout(@Nullable String... layout);

	void setLayout(char character, Supplier<ViewItem> factory);

	void setLayout(char character, Consumer<ViewItem> factory);

	/**
	 * The layout signature state of this context.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @return The layout signature state of this context.
	 */
	@ApiStatus.Internal
	boolean isLayoutSignatureChecked();

	/**
	 * Marks layout signature state of this context as checked.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param layoutSignatureChecked The new layout signature state.
	 */
	@ApiStatus.Internal
	void setLayoutSignatureChecked(boolean layoutSignatureChecked);

	@ApiStatus.Internal
	Stack<Integer> getLayoutItemsLayer();

	@ApiStatus.Internal
	void setLayoutItemsLayer(Stack<Integer> layoutItemsLayer);

	@ApiStatus.Internal
	Deque<ViewItem> getReservedItems();

	/**
	 * Converts this context to a paginated view.
	 * <p>
	 * Only works if the view that originated this context {@link #isPaginated() is paginated}.
	 *
	 * @param <T> The pagination item type.
	 * @return This context as a PaginatedVirtualView.
	 * @throws IllegalStateException If this view is not paginated.
	 */
	<T> PaginatedVirtualView<T> paginated();

	@ApiStatus.Internal
	default boolean isPaginated() {
		return this instanceof PaginatedVirtualView;
	}

	/**
	 * Attempts to resolve an item at a specified index.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param index         The item index.
	 * @param resolveOnRoot Search on root in view itself.
	 * @return The item at the specified index.
	 */
	@ApiStatus.Internal
	ViewItem resolve(int index, boolean resolveOnRoot);

	/**
	 * Renders this view.
	 */
	void render();

	/**
	 * Renders this view in the specified context.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param context The context.
	 */
	@ApiStatus.Internal
	@ApiStatus.OverrideOnly
	void render(@NotNull ViewContext context);

	/**
	 * Updates this view.
	 */
	void update();

	/**
	 * Updates this view in the specified context.
	 *
	 * <p><b><i> This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided. </i></b>
	 *
	 * @param context The context.
	 */
	@ApiStatus.Internal
	@ApiStatus.OverrideOnly
	void update(@NotNull ViewContext context);

}
