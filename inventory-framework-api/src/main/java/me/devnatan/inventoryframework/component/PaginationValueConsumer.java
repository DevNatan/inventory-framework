package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a pagination element mapping operation that accepts three arguments and returns no result.
 * <p>
 * This is a {@link FunctionalInterface functional interface} whose functional method is {@link #accept(Object, Object, int, Object)}.
 *
 * @param <CONTEXT> Type of the pagination context
 * @param <BUILDER> Builder used to build the paginated element
 * @param <V> The value that represents the current element being paginated
 */
@FunctionalInterface
public interface PaginationValueConsumer<CONTEXT, BUILDER, V> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param context The pagination context.
     * @param builder The builder used to modify the element being paginated
     * @param index The index of the element being paginated in the pagination
     * @param value The value that represents the current element being paginated
     */
    void accept(@NotNull CONTEXT context, @NotNull BUILDER builder, int index, @NotNull V value);
}
