package me.devnatan.inventoryframework.event;

@FunctionalInterface
public interface EventListener<T> {

    void call(T event);
}
