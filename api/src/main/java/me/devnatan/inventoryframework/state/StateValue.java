package me.devnatan.inventoryframework.state;

import lombok.Data;
import org.jetbrains.annotations.UnknownNullability;

/**
 * Represents the value of a {@link State} for a single {@link StateValueHost}.
 */
@Data
public abstract class StateValue {

    // Inherited id from State
    private final long id;

    /**
     * The id of this state on its current host.
     *
     * @return The state id.
     */
    public final long getId() {
        return id;
    }

    /**
     * The current state value.
     * <p>
     * The value returned and consistency with values returned by the same earlier is unknown as
     * this is implementation defined.
     *
     * @return The current state value.
     */
    @UnknownNullability
    abstract Object get();

    /**
     * Sets the new state value.
     *
     * @param value The new value.
     * @throws StateException If this value can't be mutated.
     */
    abstract void set(Object value);
}
