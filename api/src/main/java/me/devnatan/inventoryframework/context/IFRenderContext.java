package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.component.ItemBuilder;
import org.jetbrains.annotations.NotNull;

public interface IFRenderContext<B extends ItemBuilder<B>> extends IFConfinedContext {

    IFContext getParent();

    /**
     * This allows access the current configuration with the possibility to change it only for that
     * context.
     * <p>
     * By default, all contexts inherit their root configuration, context configuration always takes
     * precedence over root.
     * <p>
     * Options that change the nature of the container are not allowed to be modifier as the
     * container has already been created at that point.
     *
     * @return The current context configuration.
     */
    @NotNull
    ViewConfigBuilder modifyConfig();

    /**
     * Adds an item to a specific slot in the context container.
     *
     * @param slot The slot in which the item will be positioned.
     * @return A item builder to configure the item.
     */
    @NotNull
	B slot(int slot);

    /**
     * Adds an item at the specific column and ROW (X, Y) in that context's container.
     *
     * @param row    The row (Y) in which the item will be positioned.
     * @param column The column (X) in which the item will be positioned.
     * @return A item builder to configure the item.
     */
    @NotNull
	B slot(int row, int column);

    /**
     * Adds an item to the first slot of this context's container.
     *
     * @return A item builder to configure the item.
     */
    @NotNull
	B firstSlot();

    /**
     * Adds an item to the first slot of this context's container.
     *
     * @return A item builder to configure the item.
     */
    @NotNull
	B lastSlot();

    // TODO doc
    @NotNull
	B availableSlot();

    @NotNull
	B layoutSlot(String character);
}
