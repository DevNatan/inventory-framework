package me.saiintbrisson.minecraft;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * Mutable class that represents an item in a slot of a container.
 */
@ToString
@Setter
@Getter
public final class ViewItem {

	static final int UNSET = -1;
	public static final int AVAILABLE = -2;
    private static final long NO_INTERVAL = -3;

    enum State {
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
    private String referenceKey;

    @Setter(AccessLevel.PUBLIC)
    private boolean closeOnClick, cancelOnClick, cancelOnShiftClick;
    private ViewItemHandler renderHandler, updateHandler;
    private Consumer<ViewSlotClickContext> clickHandler;
    private Consumer<ViewSlotMoveContext> moveInHandler, moveOutHandler;
    private Consumer<ViewSlotContext> itemHoldHandler;
    private BiConsumer<ViewSlotContext, ViewSlotContext> itemReleaseHandler;
    private Map<String, Object> data;
    private long updateIntervalInTicks = NO_INTERVAL;
    private ViewItem overlay;

    /**
     * Creates a new ViewItem instance.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    public ViewItem() {
        this(-1);
    }

    /**
     * Creates a new ViewItem instance with a defined slot.
     *
     * @param slot The slot that this item will be placed initially.
     */
    public ViewItem(int slot) {
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
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem withItem(Object fallbackItem) {
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
    @Contract(mutates = "this")
    public void setItem(Object fallbackItem) {
        if (fallbackItem instanceof ViewItem || fallbackItem instanceof ItemWrapper)
            throw new IllegalStateException("Fallback item cannot be a ViewItem or ItemWrapper");

        this.item = fallbackItem;
    }

    /**
     * Determines whether the interaction should be canceled based on the current value of the {@link
     * #cancelOnClick} property, as an actor clicks on this item.
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
     * @param cancelOnClick whether the interaction should be canceled as an actor clicks on this
     *                      item.
     * @return This item.
     */
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem withCancelOnClick(boolean cancelOnClick) {
        setCancelOnClick(cancelOnClick);
        return this;
    }

    /**
     * Determines whether the interaction should be canceled based on the current value of the {@link
     * #cancelOnShiftClick} property, as an actor shift clicks on this item.
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
     * Determines whether the container should be closed based on the current value of the {@link
     * #closeOnClick} property, __AFTER__ this item is clicked.
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
    @Contract("_, _ -> this")
    public ViewItem withData(@NotNull String key, @NotNull Object value) {
        if (data == null) data = new HashMap<>();

        data.put(key, value);
        return this;
    }

    /**
     * Defines the reference key for this item.
     *
     * <p>Reference keys can be used to get an instance of a slot context whose defined item will be
     * this item, that is, you can later reference this item in an unknown handler in your code and
     * update this item manually, for example, if necessary.
     *
     * <pre>
     *  slot(...).referencedBy("my-item");
     *
     *  // Now you can update your item somewhere
     *  slot(...).onClick(click -&#62; {
     *      ViewSlotContext myItemContext = click.ref("my-item");
     *      myItemContext.updateSlot();
     *  });
     * </pre>
     *
     * <p>To get the instance a slot through a reference key, you will need a context.
     *
     * @param key The item reference key.
     * @return This item.
     * @see ViewContext#ref(String)
     */
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem referencedBy(@Nullable String key) {
        if (isPaginationItem()) throw new IllegalStateException("References are not yet supported in paginated items.");

        setReferenceKey(key);
        return this;
    }

    /**
     * Called when the item is rendered.
     *
     * <p>This handler is called every time the item or the view that owns it is updated.
     *
     * <p>It is allowed to change the item that will be displayed in this handler using the context
     * mutation functions, e.g.: {@link ViewSlotContext#setItem(Object)}.
     *
     * <p>An item can be re-rendered individually using {@link ViewSlotContext#updateSlot()}.
     *
     * @param handler The render handler.
     * @return This item.
     */
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem onRender(@Nullable ViewItemHandler handler) {
        setRenderHandler(handler);
        return this;
    }

    /**
     * Called when the item is updated.
     *
     * <p>It is allowed to change the item that will be displayed in this handler using the context
     * mutation functions, e.g.: {@link ViewSlotContext#setItem(Object)}.
     *
     * <p>An item can be updated individually using {@link ViewSlotContext#updateSlot()}.
     *
     * @param handler The update handler.
     * @return This item.
     */
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem onUpdate(@Nullable ViewItemHandler handler) {
        setUpdateHandler(handler);
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
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem onClick(@Nullable Consumer<ViewSlotClickContext> handler) {
        setClickHandler(handler);
        return this;
    }

    /**
     * Called when this item is moved from outside to inside the view's container.
     *
     * <p>This handler requires the <a
     * href="https://github.com/DevNatan/inventory-framework/tree/main/feature-move-io">feature-move-io</a>
     * feature module to be enabled to work properly.
     *
     * <p>**Using item mutation functions in this handler is not allowed.**
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
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem onItemHold(@Nullable Consumer<ViewSlotContext> handler) {
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
    @Contract(value = "_ -> this", mutates = "this")
    public ViewItem onItemRelease(@Nullable BiConsumer<ViewSlotContext, ViewSlotContext> handler) {
        setItemReleaseHandler(handler);
        return this;
    }

    /**
     * Returns <code>true</code> if this is an item whose slot is dynamically defined during the
     * lifecycle of the view to which it belongs, or <code>false</code> if it is static and its slot
     * was previously defined during its initialization.
     *
     * @return If this is a dynamic item.
     */
    public boolean isDynamic() {
        return slot == AVAILABLE || isPaginationItem();
    }
}
