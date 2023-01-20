package me.devnatan.inventoryframework;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotMoveContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Mutable class that represents an item in a slot of a container.
 * <p>
 * <b><i> Setters of this class are internal inventory-framework API that should not be used from
 * outside of this library. No compatibility guarantees are provided. </i></b>
 */
@ToString
@Setter // TODO mark setters as @ApiStatus.Internal
@Getter()
public class IFItem {

	public static final int UNSET = -1;
	public static final int AVAILABLE = -2;
	public static final long NO_INTERVAL = -3;

	public enum State {
		UNDEFINED,
		HOLDING
	}

	/**
	 * The fallback item stack that will be rendered if a function that can render is not defined or
	 * if a function that can render does not render an item.
	 */
	private Object item;

	@Getter(AccessLevel.PUBLIC)
	private int slot;

	private State state = State.UNDEFINED;
	private boolean paginationItem;
	private boolean navigationItem;
	private String referenceKey;

	@Setter(AccessLevel.PUBLIC)
	private boolean closeOnClick, cancelOnClick, cancelOnShiftClick;
	private ViewItemHandler renderHandler, updateHandler;
	private Consumer<IFSlotClickContext> clickHandler;
	private Consumer<IFSlotContext> itemHoldHandler;
	private BiConsumer<IFSlotContext, IFSlotContext> itemReleaseHandler;
	private Map<String, Object> data;
	private long updateIntervalInTicks = NO_INTERVAL;
	private IFItem overlay;
	private boolean removed;

	/**
	 * Creates a new ViewItem instance.
	 *
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 */
	@ApiStatus.Internal
	public IFItem() {
		this(-1);
	}

	/**
	 * Creates a new ViewItem instance with a defined slot.
	 *
	 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
	 * this library. No compatibility guarantees are provided.</i></b>
	 *
	 * @param slot The slot that this item will be placed initially.
	 */
	@ApiStatus.Internal
	public IFItem(int slot) {
		this.slot = slot;
	}

	/**
	 * Sets the slot that this item will be positioned.
	 *
	 * @param slot The new item slot.
	 * @return This item.
	 * @throws IllegalStateException If this item is not a navigation item.
	 */
	public IFItem withSlot(int slot) {
		if (!isNavigationItem())
			throw new IllegalStateException("Only navigation item slot can be changed.");

		this.slot = slot;
		return this;
	}

	/**
	 * Schedules this item to be updated every a defined interval.
	 *
	 * @param intervalInTicks The item update interval in ticks.
	 * @return This item.
	 */
	public IFItem scheduleUpdate(@Range(from = NO_INTERVAL, to = Long.MAX_VALUE) long intervalInTicks) {
		this.updateIntervalInTicks = Math.max(NO_INTERVAL, intervalInTicks);
		return this;
	}

	/**
	 * Schedules this item to be updated every a defined interval.
	 *
	 * @param duration The item update interval.
	 * @return This item.
	 */
	public IFItem scheduleUpdate(@Nullable Duration duration) {
		if (duration == null) this.updateIntervalInTicks = NO_INTERVAL;
		else this.updateIntervalInTicks = duration.getSeconds() * 20L;
		return this;
	}

	/**
	 * Defines the item that will be used as fallback for rendering in the slot where this item is
	 * positioned. The fallback item is always static.
	 *
	 * <p>The function of the fallback item is to provide an alternative if the item's rendering
	 * functions are not quenched, thus returning the rendering to the fallback item.
	 *
	 * <pre>{@code
	 * slot(30)
	 * 	   .withItem(...)
	 *     .onRender(render -> {
	 *         render.setItem(someCondition ? null : item);
	 *     })
	 *     .onUpdate(update -> {
	 *         update.setItem(someCondition ? null : item);
	 *     });
	 * }</pre>
	 *
	 * <p>If neither of the above two conditions are satisfied, the fallback item will be rendered,
	 * otherwise the item defined in the handlers will be rendered.
	 *
	 * @param fallbackItem The new fallback item stack.
	 * @return This item.
	 */
	public IFItem withItem(Object fallbackItem) {
		setItem(fallbackItem);
		return this;
	}

	/**
	 * Defines the item that will be used as fallback for rendering in the slot where this item is
	 * positioned. The fallback item is always static.
	 *
	 * <p>The function of the fallback item is to provide an alternative if the item's rendering
	 * functions are not quenched, thus returning the rendering to the fallback item.
	 *
	 * <pre>{@code
	 * slot(30)
	 * 	   .withItem(...)
	 *     .onRender(render -> {
	 *         render.setItem(someCondition ? null : item);
	 *     })
	 *     .onUpdate(update -> {
	 *         update.setItem(someCondition ? null : item);
	 *     });
	 * }</pre>
	 *
	 * <p>If neither of the above two conditions are satisfied, the fallback item will be rendered,
	 * otherwise the item defined in the handlers will be rendered.
	 *
	 * @param fallbackItem The new fallback item stack.
	 */
	public void setItem(Object fallbackItem) {
		if (fallbackItem instanceof IFItem || fallbackItem instanceof ItemWrapper)
			throw new IllegalStateException("Fallback item cannot be a ViewItem or ItemWrapper");

		this.item = fallbackItem;
	}

	/**
	 * Determines whether the interaction should be canceled based on the current value of the {@link
	 * #cancelOnClick} property, as an actor clicks on this item.
	 *
	 * @return This item.
	 */
	public IFItem cancelOnClick() {
		return withCancelOnClick(!isCancelOnClick());
	}

	/**
	 * Determines whether the interaction should be canceled as an actor clicks on this item.
	 *
	 * @param cancelOnClick whether the interaction should be canceled as an actor clicks on this
	 *                      item.
	 * @return This item.
	 */
	public IFItem withCancelOnClick(boolean cancelOnClick) {
		setCancelOnClick(cancelOnClick);
		return this;
	}

	/**
	 * Determines whether the interaction should be canceled based on the current value of the {@link
	 * #cancelOnShiftClick} property, as an actor shift clicks on this item.
	 *
	 * @return This item.
	 */
	public IFItem cancelOnShiftClick() {
		return withCancelOnShiftClick(!isCancelOnShiftClick());
	}

	/**
	 * Determines whether the interaction should be canceled as an actor shift clicks on this item.
	 *
	 * @param cancelOnShiftClick whether the interaction should be canceled as an actor shift clicks
	 *                           on this item.
	 * @return This item.
	 */
	public IFItem withCancelOnShiftClick(boolean cancelOnShiftClick) {
		setCancelOnShiftClick(cancelOnShiftClick);
		return this;
	}

	/**
	 * Determines whether the container should be closed based on the current value of the {@link
	 * #closeOnClick} property, __AFTER__ this item is clicked.
	 *
	 * @return This item.
	 */
	public IFItem closeOnClick() {
		return withCloseOnClick(!isCloseOnClick());
	}

	/**
	 * Determines whether the container should be closed AFTER this item is clicked.
	 *
	 * @param closeOnClick Whether the container should be closed when this item is clicked.
	 * @return This item.
	 */
	public IFItem withCloseOnClick(boolean closeOnClick) {
		setCloseOnClick(closeOnClick);
		return this;
	}

	/**
	 * Adds a new user-defined property to this item.
	 *
	 * <p>User-defined properties can be used to persist data that can be retrieved later even after
	 * several actions applied to that item.
	 *
	 * <p>An example of user-defined data persistence is for post-moving identification of an item
	 * inside the container, you can define a data in this item and as soon as the actor moves it the
	 * data will remain there, and you can use it any way you want.
	 *
	 * <pre>
	 *  slot(...).withCancelOnClick(false).withData("name", "Anna");
	 *  slot(...).withCncelOnClick(false).withData("name", "James");
	 *
	 *  &#64;Override
	 *  protected void onItemHold(ViewSlotContext context) {
	 *      String name = context.data("name");
	 *      ...
	 *  }
	 * </pre>
	 *
	 * @param key   The property key.
	 * @param value The property value.
	 * @deprecated Use {@link #withData(String, Object)} instead.
	 */
	@Deprecated
	public void setData(@NotNull String key, @NotNull Object value) {
		withData(key, value);
	}

	/**
	 * Adds a new user-defined property to this item.
	 *
	 * <p>User-defined properties can be used to persist data that can be retrieved later even after
	 * several actions applied to that item.
	 *
	 * <p>An example of user-defined data persistence is for post-moving identification of an item
	 * inside the container, you can define a data in this item and as soon as the actor moves it the
	 * data will remain there, and you can use it any way you want.
	 *
	 * <pre><code>
	 * slot(1, ...).withData("name", "Anna");
	 * slot(2, ...).withData("name", "James");
	 * slot(3, ...);
	 *
	 * &#64;Override
	 * protected void onItemHold(ViewSlotContext context) {
	 *     String name = context.getItemData("name");
	 *     // returns "Anna", "James" or null
	 * }
	 * </code></pre>
	 *
	 * @param key   The property key.
	 * @param value The property value.
	 * @return This item.
	 */
	public IFItem withData(@NotNull String key, @NotNull Object value) {
		if (data == null) data = new HashMap<>();

		data.put(key, value);
		return this;
	}

	/**
	 * Defines the reference key for this item.
	 * <p>
	 * Reference keys can be used to get an instance of a slot context whose defined item will be
	 * this item, that is, you can later reference this item in an unknown handler in your code and
	 * update this item manually, for example, if necessary.
	 * <pre>{@code
	 * slot(...).onClick(click -> {
	 *     ViewSlotContext myItemContext = click.ref("my-item");
	 * });
	 * }</pre>
	 * <p>
	 * To get the instance a slot through a reference key, you will need a context.
	 *
	 * @param key The item reference key.
	 * @return This item.
	 * @see IFContext#ref(String)
	 */
	public IFItem referencedBy(@Nullable String key) {
		if (isPaginationItem())
			throw new IllegalStateException("References are not yet supported in paginated items.");

		setReferenceKey(key);
		return this;
	}

	/**
	 * Called when the item is rendered.
	 *
	 * <p>This handler is called every time the item or the view that owns it is updated.
	 *
	 * <p>It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link IFSlotContext#setItem(Object)}.
	 *
	 * <p>An item can be re-rendered individually using {@link IFSlotContext#updateSlot()}.
	 *
	 * @param handler The render handler.
	 * @return This item.
	 */
	public IFItem onRender(@Nullable ViewItemHandler handler) {
		setRenderHandler(handler);
		return this;
	}

	/**
	 * Called when the item is rendered.
	 * <p>
	 * Shortcut to {@code onRender(render -> render.setItem(itemFactory.get());}
	 *
	 * <p>This handler is called every time the item or the view that owns it is updated.
	 *
	 * <p>It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link IFSlotContext#setItem(Object)}.
	 *
	 * <p>An item can be re-rendered individually using {@link IFSlotContext#updateSlot()}.
	 *
	 * @param itemFactory The render handler item factory, the item that'll be rendered on update.
	 * @return This item.
	 */
	public IFItem rendered(@Nullable Supplier<@Nullable Object> itemFactory) {
		setRenderHandler(itemFactory == null ? null : render -> render.setItem(itemFactory.get()));
		return this;
	}

	public IFItem rendered(@Nullable Function<IFSlotContext, @Nullable Object> itemFactory) {
		setRenderHandler(itemFactory == null ? null : render -> render.setItem(itemFactory.apply(render)));
		return this;
	}

	/**
	 * Called when the item is updated.
	 *
	 * <p>It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link IFSlotContext#setItem(Object)}.
	 *
	 * <p>An item can be updated individually using {@link IFSlotContext#updateSlot()}.
	 *
	 * @param handler The update handler.
	 * @return This item.
	 */
	public IFItem onUpdate(@Nullable ViewItemHandler handler) {
		setUpdateHandler(handler);
		return this;
	}

	/**
	 * Called when the item is updated.
	 * <p>
	 * Shortcut to {@code onUpdate(update -> update.setItem(itemFactory.get());}
	 *
	 * <p>It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link IFSlotContext#setItem(Object)}.
	 *
	 * <p>An item can be updated individually using {@link IFSlotContext#updateSlot()}.
	 *
	 * @param itemFactory The update handler item factory, the item that'll be rendered on update.
	 * @return This item.
	 */
	public IFItem updated(@Nullable Supplier<@Nullable Object> itemFactory) {
		setUpdateHandler(itemFactory == null ? null : update -> update.setItem(itemFactory.get()));
		return this;
	}

	/**
	 * Called when a player clicks on the item.
	 *
	 * <p>This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 *
	 * <p>**Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The click handler.
	 * @return This item.
	 */
	public IFItem onClick(@Nullable Consumer<IFSlotClickContext> handler) {
		setClickHandler(handler);
		return this;
	}

	/**
	 * Called when a player holds an item.
	 *
	 * <p>This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 *
	 * <p>You can check if the item has been released using {@link #onItemRelease(BiConsumer)}.
	 *
	 * <p>**Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The item hold handler.
	 * @return This item.
	 */
	public IFItem onItemHold(@Nullable Consumer<IFSlotContext> handler) {
		setItemHoldHandler(handler);
		return this;
	}

	/**
	 * Called when a player releases an item.
	 *
	 * <p>This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 *
	 * <p>You can know when the item was hold using {@link #onItemHold(Consumer)}.
	 *
	 * <p>**Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The item release handler.
	 * @return This item.
	 */
	public IFItem onItemRelease(@Nullable BiConsumer<IFSlotContext, IFSlotContext> handler) {
		setItemReleaseHandler(handler);
		return this;
	}

}
