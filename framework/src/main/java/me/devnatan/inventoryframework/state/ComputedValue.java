package me.devnatan.inventoryframework.state;

import java.util.Objects;
import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

/**
 * Computed value whose value returned by the function to get the state value is always a new value
 * created by {@link ComputedValue#factory}.
 */
public final class ComputedValue extends StateValue {

    private final Supplier<Object> factory;

	ComputedValue(long id, @NotNull Supplier<Object> factory) {
		super(id);
		this.factory = factory;
	}

    @Override
    public Object get() {
        return factory.get();
    }

    @Override
    public void set(Object value) {
        throw new IllegalStateModificationException("Immutable");
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		ComputedValue that = (ComputedValue) o;
		return factory.equals(that.factory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), factory);
	}

	@Override
	public String toString() {
		return "ComputedValue{" +
			"computation=" + factory +
			"} " + super.toString();
	}
}
