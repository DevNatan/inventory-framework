package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PaginationElementConsumer<Context, Builder, V> {

    void accept(@NotNull Context context, @NotNull Builder builder, int index, @NotNull V value);
}
