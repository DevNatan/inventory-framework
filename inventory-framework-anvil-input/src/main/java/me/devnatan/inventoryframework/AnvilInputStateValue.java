package me.devnatan.inventoryframework;

import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.state.MutableValue;
import me.devnatan.inventoryframework.state.State;

class AnvilInputStateValue extends MutableValue {

    private final UnaryOperator<String> onInputChange;

    public AnvilInputStateValue(State<?> state, Object currValue, UnaryOperator<String> onInputChange) {
        super(state, currValue);
        this.onInputChange = onInputChange;
    }

    @Override
    public void set(Object value) {
        super.set(onInputChange.apply((String) value));
    }
}
