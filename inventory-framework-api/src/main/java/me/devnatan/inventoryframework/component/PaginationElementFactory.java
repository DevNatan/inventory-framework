package me.devnatan.inventoryframework.component;

@FunctionalInterface
public interface PaginationElementFactory<Context, V> {

    ComponentFactory create(Context context, int index, int slot, V value);
}
