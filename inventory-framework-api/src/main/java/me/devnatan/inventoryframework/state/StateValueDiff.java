package me.devnatan.inventoryframework.state;

import java.util.Objects;
import org.jetbrains.annotations.ApiStatus;

/**
 * <p><b><i>This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided.</i></b>
 */
@ApiStatus.Internal
public final class StateValueDiff {

    private final transient StateValueHost host;
    private final transient StateValue holderValue;

    private final Object oldValue;
    private final Object newValue;

    public StateValueDiff(StateValueHost host, StateValue holderValue, Object oldValue, Object newValue) {
        this.host = host;
        this.holderValue = holderValue;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public StateValueHost getHost() {
        return host;
    }

    public StateValue getValue() {
        return holderValue;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StateValueDiff that = (StateValueDiff) o;
        return Objects.equals(oldValue, that.oldValue) && Objects.equals(newValue, that.newValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oldValue, newValue);
    }

    @Override
    public String toString() {
        return "StateValueDiff{" + "holderValue="
                + holderValue + ", oldValue="
                + oldValue + ", newValue="
                + newValue + '}';
    }
}
