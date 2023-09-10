package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.NotNull;

/**
 * Represents an pagination element mapping operation that accepts three arguments and returns no result.
 * This is a {@link FunctionalInterface functional interface} whose functional method is {@link #accept(Object, Object, int, Object)}.
 * @param <Context> The type of the pagination context
 * @param <Builder> The builder used to build the paginated element
 * @param <V> The value that represents the current element being paginated
 */
@FunctionalInterface
public interface PaginationElementConsumer<Context, Builder, V> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param context The pagination context.
     * @param builder The builder used to modify the element being paginated
     * @param index The index of the element being paginated in the pagination
     * @param value The value that represents the current element being paginated
     */
    void accept(@NotNull Context context, @NotNull Builder builder, int index, @NotNull V value);
}
