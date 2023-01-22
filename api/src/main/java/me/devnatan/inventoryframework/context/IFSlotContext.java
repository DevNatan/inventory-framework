package me.devnatan.inventoryframework.context;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.function.Supplier;

import me.devnatan.inventoryframework.exception.InventoryModificationException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents a context in which there is a specific slot related to it, the main context
 * encompasses the entire container in an actor's view, the ViewSlotContext encapsulates a context
 * for just one slot of a container.
 *
 * <p>Methods specific to a ViewSlotContext will only apply to that slot.
 *
 * @see IFContext
 * @see IFSlotClickContext
 * @see IFSlotMoveContext
 * @see IFPaginatedSlotContext
 */
public interface IFSlotContext extends IFContext {

    /**
     * The parent context of this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return The parent context of this context.
     */
    @ApiStatus.Internal
    IFContext getParent();

    /**
     * Clears this slot from the current context.
     * <p>
     * The slot will only be cleaned on the next update, so if you want it cleaned immediately
     * update the slot using {@link #updateSlot()}.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    void clear();

    /**
     * Returns the slot position of this context in the current container.
     *
     * @return The slot position of this context.
     */
    int getSlot();

    /**
     * Updates this slot.
     */
    void updateSlot();

    /**
     * Returns the wrapper containing the item related to this context.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return The current item wrapper.
     */
//    @ApiStatus.Internal
//    @NotNull
//    ItemWrapper getItemWrapper();

    /**
     * Returns the current item of this context.
     * <p>
     * The item returned is not necessarily the item positioned in the slot, there are cases, for
     * example in {@link IFSlotMoveContext}, in which the current item may be the item the entity
     * is interacting with and not a positioned item.
     *
     * @return The current item.
     */
//    @NotNull
//    ItemWrapper getCurrentItem();

    /**
     * Sets the new item for this slot for this context.
     * <p>
     * If you need to change the item partially use {@link #updateItem(Consumer)} instead.
     *
     * @param item The new item that'll be set.
     * @throws InventoryModificationException When the container is changed.
     */
    void setItem(@Nullable Object item) throws InventoryModificationException;

    /**
     * Sets the new item for this slot for this context.
     * <p>
     * If you need to change the item partially use {@link #updateItem(Consumer)} instead.
     *
     * @param item The new item that'll be set.
     * @return This context.
     * @throws InventoryModificationException When the container is changed.
     * @deprecated Use {@link #setItem(Object)} instead.
     */
    @Deprecated
    @NotNull
    @ApiStatus.ScheduledForRemoval(inVersion = "2.5.6")
    IFSlotContext withItem(@Nullable Object item) throws InventoryModificationException;

    /**
     * Applies a patch to the current item.
     * <p>
     * This method should be used when only a partial modification is required to be applied to the
     * item, triggering an {@link AbstractVirtualView#inventoryModificationTriggered() inventory
     * modification}.
     *
     * <p>If you need to update the item completely use {@link #setItem(Object)}.
     *
     * @param updater The update function.
     */
//    void updateItem(Consumer<ItemWrapper> updater);

    /**
     * Whether this context originated from an interaction coming from the actor's container and not
     * from the view's container.
     *
     * @return If this context originated from the actor's container
     */
    boolean isOnEntityContainer();

    /**
     * Checks if the item in this context has been changed.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @return If the item in this context has been changed.
     */
    @ApiStatus.Internal
    boolean hasChanged();

    /**
     * Marks this context as changed.
     *
     * <p>Improperly changing this property can cause unexpected side effects.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param changed If the context item was changed.
     */
    @ApiStatus.Internal
    void setChanged(boolean changed);

    /**
     * Returns the value of a user-defined property for the item of this slot context or throws an
     * exception if the property has not been set.
     * <p>
     * The properties are previously defined by {@link ViewItem#withData(String, Object)}.
     *
     * @param key The property key.
     * @param <T> The property value type.
     * @return The property value.
     * @throws NoSuchElementException If the property has not been set.
     */
    @NotNull
    <T> T getItemData(@NotNull String key) throws NoSuchElementException;

    /**
     * Returns the value of a user-defined property for the item of this slot context or throws an
     * exception if the property has not been set.
     * <p>
     * The properties are previously defined by {@link ViewItem#withData(String, Object)}.
     *
     * @param key          The property key.
     * @param defaultValue The default value factory if property is not found.
     * @param <T>          The property value type.
     * @return The property value or the default value.
     */
    @UnknownNullability("Return value is defined by the availability of the property or by the"
            + " [defaultValue] parameter in case of fallback which can be null or not.")
    <T> T getItemData(@NotNull String key, @NotNull Supplier<T> defaultValue);

    /**
     * Returns the value of a user-defined property for the item of this slot context or throws an
     * exception if the property has not been set.
     * <p>
     * The properties are previously defined by {@link ViewItem#withData(String, Object)}.
     *
     * @param key The property key.
     * @param <T> The property value type.
     * @return The property value or null.
     */
    @Nullable
    <T> T getItemDataOrNull(@NotNull String key);

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    boolean isRegistered();
}
