package me.saiintbrisson.minecraft.event;

@FunctionalInterface
public interface EventListener<T> {

    void call(T event);
}
