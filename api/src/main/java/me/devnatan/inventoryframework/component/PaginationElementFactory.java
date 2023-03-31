package me.devnatan.inventoryframework.component;

@FunctionalInterface
public interface PaginationElementFactory<T> {

    ComponentFactory create(int index, int slot, T value);
}
