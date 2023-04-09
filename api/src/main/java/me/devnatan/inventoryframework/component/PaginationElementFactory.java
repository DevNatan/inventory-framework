package me.devnatan.inventoryframework.component;

@FunctionalInterface
public interface PaginationElementFactory<T, V> {

    ComponentFactory create(T context, int index, int slot, V value);
}
