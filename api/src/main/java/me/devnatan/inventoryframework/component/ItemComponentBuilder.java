package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public interface ItemComponentBuilder<S extends ItemComponentBuilder<S>> extends ComponentBuilder<S> {

    /**
     * Sets the slot that the item will be positioned.
     *
     * @param slot The item slot.
     * @return This item builder.
     */
    S withSlot(int slot);

    /**
     * Watches a state, updating that item every time the state is updated.
     *
     * @param state The state to watch.
     * @return This item builder.
     */
    S watch(State<?>... states);

    @ApiStatus.Internal
    boolean isContainedWithin(int position);
}
