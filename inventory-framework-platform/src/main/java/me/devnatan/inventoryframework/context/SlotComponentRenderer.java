package me.devnatan.inventoryframework.context;

import java.util.function.BiConsumer;
import me.devnatan.inventoryframework.component.PlatformComponentBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface SlotComponentRenderer<C, B> {

    /**
     * Creates a new item builder without a specified slot.
     * <p>
     * This function is for creating items whose slot is set dynamically during item rendering.
     * <pre>{@code
     * unsetSlot().onRender(render -> {
     *     render.setItem(...);
     *     render.setSlot(...);
     * });
     * }</pre>
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @return An item builder to configure the item.
     */
    @ApiStatus.Experimental
    B unsetSlot();

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    <T extends PlatformComponentBuilder<T, C>> void unsetSlotComponent(T componentBuilder);

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    B slot(int slot);

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return An item builder to configure the item.
     */
    B slot(int row, int column);

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    <T extends PlatformComponentBuilder<T, C>> void slotComponent(int slot, T componentBuilder);

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    <T extends PlatformComponentBuilder<T, C>> void slotComponent(int row, int column, T componentBuilder);

    /**
     * Sets an item in the first slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    B firstSlot();

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    <T extends PlatformComponentBuilder<T, C>> void firstSlotComponent(T componentBuilder);

    /**
     * Sets an item in the last slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    B lastSlot();

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    <T extends PlatformComponentBuilder<T, C>> void lastSlotComponent(T componentBuilder);

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * @return An item builder to configure the item.
     */
    B availableSlot();

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    <T extends PlatformComponentBuilder<T, C>> void availableSlotComponent(T componentBuilder);

    /**
     * Adds an item in the next available slot of this context's container.
     *
     * <pre>{@code
     * availableSlot((index, builder) -> builder.withItem(...));
     * }</pre>
     *
     * @param factory A factory to create the item builder to configure the item.
     *                The first parameter is the iteration index of the available slot.
     */
    void availableSlot(@NotNull BiConsumer<Integer, B> factory);

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * @param character The layout character target.
     * @return An item builder to configure the item.
     */
    B layoutSlot(char character);

    /**
     * Defines the item that will represent a character provided in the context layout.
     *
     * <pre>{@code
     * layoutSlot('F', (index, builder) -> builder.withItem(...));
     * }</pre>
     *
     * @param character The layout character target.
     */
    void layoutSlot(char character, BiConsumer<Integer, B> factory);

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    <T extends PlatformComponentBuilder<T, C>> void layoutSlot(char character, T componentBuilder);

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    B resultSlot();
}
