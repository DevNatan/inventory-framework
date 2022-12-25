package me.devnatan.inventoryframework.state;

public interface StateValueHolder {

    State<?> getState();

    Object get();

    void set(Object newValue);
}
