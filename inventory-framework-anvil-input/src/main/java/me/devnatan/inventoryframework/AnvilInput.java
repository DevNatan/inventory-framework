package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.state.MutableValue;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValueFactory;
import org.jetbrains.annotations.ApiStatus;

public final class AnvilInput {

    private AnvilInput() {}

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public static State<String> anvilInputState(String initialInput) {
        final long id = State.next();
        final StateValueFactory factory = (host, state) -> new MutableValue(state, initialInput);
        return new AnvilInputState(id, factory);
    }
}
