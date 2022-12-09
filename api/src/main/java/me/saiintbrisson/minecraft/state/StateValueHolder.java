package me.saiintbrisson.minecraft.state;

public interface StateValueHolder {

    State<?> getState();

    Object get();

    void set(Object newValue);
}
