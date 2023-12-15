package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.NotNull;

/**
 * Represents a pagination element mapping operation that accepts three arguments and returns no result.
 * <p>
 * This is a {@link FunctionalInterface functional interface} whose functional method is {@link #accept(Object, int, Object)}.
 *
 * @param <CONTEXT> Type of the pagination context
 * @param <V> The value that represents the current element being paginated
 */
@FunctionalInterface
public interface PaginationValueComponentFactory<CONTEXT, V> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param context The pagination context.
     * @param index The index of the element being paginated in the pagination
     * @param value The value that represents the current element being paginated
     */
    ComponentBuilder accept(@NotNull CONTEXT context, int index, @NotNull V value);
}
