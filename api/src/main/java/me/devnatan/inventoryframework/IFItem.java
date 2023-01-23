package me.devnatan.inventoryframework;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@Getter
@Setter
public abstract class IFItem<S extends IFItem<?>> {

    public static final int UNSET = -1;
    public static final int AVAILABLE = -2;
    public static final long NO_INTERVAL = -1;

    public enum State {
        UNDEFINED,
        HOLDING
    }

    private Object item;
    private int slot;
    private State state = State.UNDEFINED;
    private String referenceKey;
    private Map<String, Object> data;
    private IFItem<?> overlay;
    private boolean removed, navigationItem, closeOnClick, cancelOnClick, cancelOnShiftClick;
    private long updateIntervalInTicks = NO_INTERVAL;

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
    public S withSlot(int slot) {
        if (!isNavigationItem()) throw new IllegalStateException("Only navigation item slot can be changed.");

        this.slot = slot;
        return (S) this;
    }

    /**
     * Schedules this item to be updated every a defined interval.
     *
     * @param intervalInTicks The item update interval in ticks.
     * @return This item.
     */
    public S scheduleUpdate(@Range(from = NO_INTERVAL, to = Long.MAX_VALUE) long intervalInTicks) {
        this.updateIntervalInTicks = Math.max(NO_INTERVAL, intervalInTicks);
        return (S) this;
    }

    /**
     * Schedules this item to be updated every a defined interval.
     *
     * @param duration The item update interval.
     * @return This item.
     */
    public S scheduleUpdate(@Nullable Duration duration) {
        if (duration == null) this.updateIntervalInTicks = NO_INTERVAL;
        else this.updateIntervalInTicks = duration.getSeconds() * 20L;
        return (S) this;
    }

    /**
     * Determines whether the interaction should be canceled based on the current value of the {@link
     * #cancelOnClick} property, as an actor clicks on this item.
     *
     * @return This item.
     */
    public S cancelOnClick() {
        return withCancelOnClick(!isCancelOnClick());
    }

    /**
     * Determines whether the interaction should be canceled as an actor clicks on this item.
     *
     * @param cancelOnClick whether the interaction should be canceled as an actor clicks on this
     *                      item.
     * @return This item.
     */
    public S withCancelOnClick(boolean cancelOnClick) {
        setCancelOnClick(cancelOnClick);
        return (S) this;
    }

    /**
     * Determines whether the interaction should be canceled based on the current value of the {@link
     * #cancelOnShiftClick} property, as an actor shift clicks on this item.
     *
     * @return This item.
     */
    public S cancelOnShiftClick() {
        return withCancelOnShiftClick(!isCancelOnShiftClick());
    }

    /**
     * Determines whether the interaction should be canceled as an actor shift clicks on this item.
     *
     * @param cancelOnShiftClick whether the interaction should be canceled as an actor shift clicks
     *                           on this item.
     * @return This item.
     */
    public S withCancelOnShiftClick(boolean cancelOnShiftClick) {
        setCancelOnShiftClick(cancelOnShiftClick);
        return (S) this;
    }

    /**
     * Determines whether the container should be closed based on the current value of the {@link
     * #closeOnClick} property, __AFTER__ this item is clicked.
     *
     * @return This item.
     */
    public S closeOnClick() {
        return withCloseOnClick(!isCloseOnClick());
    }

    /**
     * Determines whether the container should be closed AFTER this item is clicked.
     *
     * @param closeOnClick Whether the container should be closed when this item is clicked.
     * @return This item.
     */
    public S withCloseOnClick(boolean closeOnClick) {
        setCloseOnClick(closeOnClick);
        return (S) this;
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
    public S withData(@NotNull String key, @NotNull Object value) {
        if (data == null) data = new HashMap<>();

        data.put(key, value);
        return (S) this;
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
    public S referencedBy(@Nullable String key) {
        setReferenceKey(key);
        return (S) this;
    }

    public abstract Consumer<? super IFSlotContext> getRenderHandler();

    public abstract Consumer<? super IFSlotContext> getUpdateHandler();

    public abstract Consumer<? super IFSlotClickContext> getClickHandler();

    public abstract Consumer<? super IFSlotClickContext> getHoldHandler();

    public abstract BiConsumer<? super IFSlotClickContext, ? extends IFSlotClickContext> getReleaseHandler();
}
