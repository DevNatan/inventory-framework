package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.state.MutableValue;

class AnvilInputStateValue extends MutableValue {

    private final AnvilInputConfig config;

    public AnvilInputStateValue(long internalId, AnvilInputConfig config) {
        super(internalId, config.initialInput);
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
