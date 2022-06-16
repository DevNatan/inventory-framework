package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Mutable class that represents an item in a slot of a container.
 */
@ToString
@Setter(AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
public final class ViewItem {

	private static final long NO_INTERVAL = -1;

	enum State {UNDEFINED, HOLDING}


	/**
	 * The fallback item stack that will be rendered if a function that can render is not defined
	 * or if a function that can render does not render an item.
	 */
	private Object item;

	private int slot;
	private State state = State.UNDEFINED;
	private boolean paginationItem;
	private String referenceKey;
	private boolean closeOnClick, cancelOnClick, cancelOnShiftClick;
	private Consumer<ViewSlotContext> renderHandler, updateHandler, clickHandler;
	private Consumer<ViewSlotMoveContext> moveInHandler, moveOutHandler;
	private Consumer<ViewSlotContext> itemHoldHandler;
	private BiConsumer<ViewSlotContext, ViewSlotContext> itemReleaseHandler;
	private Map<String, Object> data;
	private long updateIntervalInTicks = NO_INTERVAL;

	/**
	 * Creates a new ViewItem instance.
	 *
	 * @deprecated You cannot instantiate a ViewItem, use {@link AbstractView#item()} instead.
	 */
	@SuppressWarnings("DeprecatedIsStillUsed")
	@Deprecated
	public ViewItem() {
		this(-1);
	}

	/**
	 * Creates a new ViewItem instance with a defined slot.
	 *
	 * @param slot The slot that this item will be placed initially.
	 */
	ViewItem(int slot) {
		this.slot = slot;
	}

	/**
	 * Schedules this item to be updated every a defined interval.
	 *
	 * @param intervalInTicks The item update interval in ticks.
	 * @return This item.
	 */
	@ApiStatus.Experimental
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem scheduleUpdate(@Range(from = NO_INTERVAL, to = Long.MAX_VALUE) long intervalInTicks) {
		this.updateIntervalInTicks = Math.max(NO_INTERVAL, intervalInTicks);
		return this;
	}

	/**
	 * Schedules this item to be updated every a defined interval.
	 *
	 * @param duration The item update interval.
	 * @return This item.
	 */
	@ApiStatus.Experimental
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem scheduleUpdate(@Nullable Duration duration) {
		if (duration == null)
			this.updateIntervalInTicks = NO_INTERVAL;
		else
			this.updateIntervalInTicks = duration.getSeconds() * 20L;
		return this;
	}

	/**
	 * Defines the item that will be used as fallback for rendering in the slot where this item
	 * is positioned. The fallback item is always static.
	 * <p>
	 * The function of the fallback item is to provide an alternative if the item's rendering
	 * functions are not quenched, thus returning the rendering to the fallback item.
	 * <pre>
	 * {@code
	 * slot(30)
	 * 	   .withItem(...)
	 *     .onRender(render -> {
	 *         render.setItem(someCondition ? null : item);
	 *     })
	 *     .onUpdate(update -> {
	 *         update.setItem(someCondition ? null : item);
	 *     });
	 * }
	 * </pre>
	 * If neither of the above two conditions are satisfied, the fallback item will be rendered,
	 * otherwise the item defined in the handlers will be rendered.
	 *
	 * @param fallbackItem The new fallback item stack.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem withItem(Object fallbackItem) {
		setItem(fallbackItem);
		return this;
	}

	/**
	 * Determines whether the interaction should be canceled based on the current value of the
	 * {@link #cancelOnClick} property, as an actor clicks on this item.
	 *
	 * @return This item.
	 */
	@Contract(mutates = "this")
	public ViewItem cancelOnClick() {
		return withCancelOnClick(!isCancelOnClick());
	}

	/**
	 * Determines whether the interaction should be canceled as an actor clicks on this item.
	 *
	 * @param cancelOnClick whether the interaction should be canceled as an actor clicks on this item.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem withCancelOnClick(boolean cancelOnClick) {
		setCancelOnClick(cancelOnClick);
		return this;
	}

	/**
	 * Determines whether the interaction should be canceled based on the current value of the
	 * {@link #cancelOnShiftClick} property, as an actor shift clicks on this item.
	 *
	 * @return This item.
	 */
	@Contract(mutates = "this")
	public ViewItem cancelOnShiftClick() {
		return withCancelOnShiftClick(!isCancelOnShiftClick());
	}

	/**
	 * Determines whether the interaction should be canceled as an actor shift clicks on this item.
	 *
	 * @param cancelOnShiftClick whether the interaction should be canceled as an actor shift clicks
	 *                           on this item.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem withCancelOnShiftClick(boolean cancelOnShiftClick) {
		setCancelOnShiftClick(cancelOnShiftClick);
		return this;
	}

	/**
	 * Determines whether the container should be closed based on the current value of the {@link #closeOnClick}
	 * property, __AFTER__ this item is clicked.
	 *
	 * @return This item.
	 */
	@Contract(mutates = "this")
	public ViewItem closeOnClick() {
		return withCloseOnClick(!isCloseOnClick());
	}

	/**
	 * Determines whether the container should be closed AFTER this item is clicked.
	 *
	 * @param closeOnClick Whether the container should be closed when this item is clicked.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem withCloseOnClick(boolean closeOnClick) {
		setCloseOnClick(closeOnClick);
		return this;
	}

	/**
	 * Adds a new user-defined property to this item.
	 * <p>
	 * User-defined properties can be used to persist data that can be retrieved later even after
	 * several actions applied to that item.
	 * <p>
	 * An example of user-defined data persistence is for post-moving identification of an item
	 * inside the container, you can define a data in this item and as soon as the actor moves it
	 * the data will remain there, and you can use it any way you want.
	 * <pre>
	 * {@code
	 * slot(...).withCancelOnClick(false).withData("name", "Anna")
	 * slot(...).withCncelOnClick(false).withData("name", "James");
	 *
	 * @Override
	 * protected void onItemHold(ViewSlotContext context) {
	 *     String name = context.getItemData("name");
	 *     ...
	 * }
	 * }
	 * </pre>
	 *
	 * @param key   The property key.
	 * @param value The property value.
	 * @return This item.
	 */
	@Contract("_, _ -> this")
	public ViewItem withData(@NotNull String key, @NotNull Object value) {
		if (data == null)
			data = new HashMap<>();

		data.put(key, value);
		return this;
	}

	/**
	 * Defines the reference key for this item.
	 * <p>
	 * Reference keys can be used to get an instance of a slot context whose defined item
	 * will be this item, that is, you can later reference this item in an unknown handler
	 * in your code and update this item manually, for example, if necessary.
	 * <pre>
	 * {@code
	 * slot(...).referencedBy("my-item");
	 *
	 * // Now you can update your item somewhere
	 * slot(...).onClick(click -> {
	 *     ViewSlotContext myItemContext = click.ref("my-item");
	 *     myItemContext.updateSlot();
	 * });
	 * }
	 * </pre>
	 * To get the instance a slot through a reference key, you will need a context.
	 *
	 * @param key The item reference key.
	 * @return This item.
	 * @see ViewContext#ref(String)
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem referencedBy(@Nullable String key) {
		setReferenceKey(key);
		return this;
	}

	/**
	 * Called when the item is rendered.
	 * <p>
	 * This handler is called every time the item or the view that owns it is updated.
	 * <p>
	 * It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link ViewSlotContext#setItem(Object)}.
	 * <p>
	 * An item can be re-rendered individually using {@link ViewSlotContext#updateSlot()}.
	 *
	 * @param handler The move in handler.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onRender(@Nullable Consumer<ViewSlotContext> handler) {
		setRenderHandler(handler);
		return this;
	}

	/**
	 * Called when the item is updated.
	 * <p>
	 * It is allowed to change the item that will be displayed in this handler using the context
	 * mutation functions, e.g.: {@link ViewSlotContext#setItem(Object)}.
	 * <p>
	 * An item can be updated individually using {@link ViewSlotContext#updateSlot()}.
	 *
	 * @param handler The move in handler.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onUpdate(@Nullable Consumer<ViewSlotContext> handler) {
		setUpdateHandler(handler);
		return this;
	}

	/**
	 * Called when a player clicks on the item.
	 * <p>
	 * This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 * <p>
	 * **Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The move in handler.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onClick(@Nullable Consumer<ViewSlotContext> handler) {
		setClickHandler(handler);
		return this;
	}

	/**
	 * Called when the item is moved from within the view's container to another container that is
	 * not the view's container.
	 * <p>
	 * This handler requires the <a href="https://github.com/DevNatan/inventory-framework/tree/main/feature-move-io">feature-move-io</a>
	 * feature module to be enabled to work properly.
	 * <p>
	 * **Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The move in handler.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onMoveIn(@Nullable Consumer<ViewSlotMoveContext> handler) {
		setMoveInHandler(handler);
		return this;
	}

	/**
	 * Called when this item is moved from outside to inside the view's container.
	 * <p>
	 * This handler requires the <a href="https://github.com/DevNatan/inventory-framework/tree/main/feature-move-io">feature-move-io</a>
	 * feature module to be enabled to work properly.
	 * <p>
	 * **Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The move out handler.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onMoveOut(@Nullable Consumer<ViewSlotMoveContext> handler) {
		setMoveOutHandler(handler);
		return this;
	}

	/**
	 * Called when a player holds an item.
	 * <p>
	 * This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 * <p>
	 * You can check if the item has been released using {@link #onItemRelease(BiConsumer)}.
	 * <p>
	 * **Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The item hold handler.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onItemHold(@Nullable Consumer<ViewSlotContext> handler) {
		setItemHoldHandler(handler);
		return this;
	}

	/**
	 * Called when a player releases an item.
	 * <p>
	 * This handler works on any container that the actor has access to and only works if the
	 * interaction has not been cancelled.
	 * <p>
	 * You can know when the item was hold using {@link #onItemHold(Consumer)}.
	 * <p>
	 * **Using item mutation functions in this handler is not allowed.**
	 *
	 * @param handler The item release handler.
	 * @return This item.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	public ViewItem onItemRelease(@Nullable BiConsumer<ViewSlotContext, ViewSlotContext> handler) {
		setItemReleaseHandler(handler);
		return this;
	}

}