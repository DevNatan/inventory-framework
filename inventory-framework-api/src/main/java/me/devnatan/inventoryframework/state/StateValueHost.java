package me.devnatan.inventoryframework.state;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * StateHost is an entity capable of storing current data from multiple states.
 */
public interface StateValueHost {

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    Map<Long, StateValue> getStateValues();

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    Map<Long, List<StateWatcher>> getStateWatchers();

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    StateValue getUninitializedStateValue(long stateId);

    /**
     * Returns the internal value of a state.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param state The target state.
     * @return Raw value from the state with the specified id.
     */
    @ApiStatus.Internal
    Object getRawStateValue(State<?> state);

    /**
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     */
    @ApiStatus.Internal
    StateValue getInternalStateValue(State<?> state);

    /**
     * Initializes the value of a state in this value host.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param id    The state id.
     * @param value The initial state value.
     */
    @ApiStatus.Internal
    void initializeState(long id, @NotNull StateValue value);

    /**
     * Updates the value of an initialized state in this value host.
     *
     * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.</i></b>
     *
     * @param id    The state id.
     * @param value The new state value.
     */
    @ApiStatus.Internal
    void updateState(long id, Object value);

    @ApiStatus.Internal
    void watchState(long id, StateWatcher listener);
}
