package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.ApiStatus;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public abstract class AbstractStateValue implements StateValue {

    protected final State<?> state;

    protected AbstractStateValue(State<?> state) {
        this.state = state;
    }

	@Override
	public long internalId() {
		return state.internalId();
	}

	@Override
    public abstract Object get();

    @Override
    public void set(Object value) {
        throw new IllegalStateModificationException("Immutable");
    }

    @Override
    public String toString() {
        return "AbstractStateValue{" + "state=" + state + '}';
    }
}
