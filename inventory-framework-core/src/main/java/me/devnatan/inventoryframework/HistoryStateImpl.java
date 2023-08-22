package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.state.BaseState;
import me.devnatan.inventoryframework.state.HistoryState;
import me.devnatan.inventoryframework.state.StateValueFactory;
import org.jetbrains.annotations.ApiStatus;

class HistoryStateImpl extends BaseState<HistoryState.History> implements HistoryState {

    /**
     * Creates a new HistoryState instance.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @param id           The state id.
     * @param valueFactory The value factory of this state.
     */
    @ApiStatus.Internal
    public HistoryStateImpl(long id, StateValueFactory valueFactory) {
        super(id, valueFactory);
    }
}
