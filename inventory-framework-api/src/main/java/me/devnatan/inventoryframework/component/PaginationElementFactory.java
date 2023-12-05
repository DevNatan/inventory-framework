package me.devnatan.inventoryframework.component;

@FunctionalInterface
public interface PaginationElementFactory<V> {

    Component create(Pagination root, int index, int slot, V value);
}
