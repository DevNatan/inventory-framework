package me.devnatan.inventoryframework.state;

public interface StateValueHolder {

    StateMarker getState();

    Object get();

    void set(Object newValue);
}
