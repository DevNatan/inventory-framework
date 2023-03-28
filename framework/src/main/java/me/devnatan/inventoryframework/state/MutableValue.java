package me.devnatan.inventoryframework.state;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class MutableValue extends StateValue {

    private Object currValue;

    public MutableValue(@NotNull State<?> state, Object currValue) {
        super(state);
        this.currValue = currValue;
    }

    @Override
    public Object get() {
        return currValue;
    }

    @Override
    public void set(Object value) {
        this.currValue = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MutableValue that = (MutableValue) o;
        return Objects.equals(currValue, that.currValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), currValue);
    }

    @Override
    public String toString() {
        return "MutableValue{" + "currValue=" + currValue + "} " + super.toString();
    }
}
