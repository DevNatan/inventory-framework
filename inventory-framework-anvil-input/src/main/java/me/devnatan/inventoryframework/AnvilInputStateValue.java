package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.state.MutableValue;
import me.devnatan.inventoryframework.state.State;

class AnvilInputStateValue extends MutableValue {

    private final AnvilInputConfig config;

    public AnvilInputStateValue(State<?> state, AnvilInputConfig config) {
        super(state, config.initialInput);
        this.config = config;
    }

    @Override
    public void set(Object value) {
        final Object newValue;
        if (config.inputChangeHandler == null) newValue = value;
        else newValue = config.inputChangeHandler.apply((String) value);

        super.set(newValue);
    }
}
