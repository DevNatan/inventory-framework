package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class to handle paging related data.
 *
 * <p>{@link #source} is the raw paging data even if the data is not of the provided type (with
 * factory).
 *
 * @param <T> The pagination source type.
 */
@Getter
@Setter
final class Paginator<T> {

    private int pageSize;
    private List<T> source;
    private Function<PaginatedViewContext<T>, List<T>> factory;
    private final boolean provided;

    @SuppressWarnings("unchecked")
    Paginator(int pageSize, @NotNull Object source) {
        Function<PaginatedViewContext<T>, List<T>> _factory = null;
        List<T> _source = null;
        this.pageSize = pageSize;

        if (source instanceof List) _source = (List<T>) source;
        else if (source instanceof Function)
            _factory = (Function<PaginatedViewContext<T>, List<T>>) source;
        else
            throw new IllegalArgumentException(
                    "Unsupported pagination source type: " + source.getClass().getName());

        this.factory = _factory;
        this.source = _source;
        this.provided = _factory != null;
    }

    public boolean hasPage(int currentIndex) {
        checkSource();
        return currentIndex >= 0 && currentIndex < count();
    }

    /**
     * Amount of items available in the source.
     *
     * @return The amount of items available in the source.
     * @throws IllegalStateException If source is null.
     */
    public int size() {
        checkSource();
        return source.size();
    }

    /**
     * Gets an item at a specific position from source.
     *
     * @param index The item position.
     * @return The item or null if it wasn't found.
     * @throws IllegalStateException If source is null.
     */
    public T get(int index) {
        checkSource();
        return source.get(index);
    }

    /**
     * Number of items per page.
     *
     * @return The number of items per page.
     * @throws IllegalStateException If source is null.
     */
    public int count() {
        checkSource();
        return (int) Math.ceil((double) size() / pageSize);
    }

    public CompletableFuture<List<T>> getPage(int index, @NotNull PaginatedViewContext<T> context) {
        if (source != null) return CompletableFuture.completedFuture(getPageBlocking(index));
        if (factory != null) return getPageLazy(context);

        throw new IllegalStateException(
                String.format(
                        "No source or provider available to fetch page data on index %d.", index));
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<List<T>> getPageLazy(@NotNull PaginatedViewContext<T> context) {
        final Object data = factory.apply(context);
        if (data instanceof List) {
            final List<T> contents = (List<T>) data;
            return CompletableFuture.completedFuture(
                    contents.isEmpty() ? Collections.emptyList() : new ArrayList<>(contents));
        }

        if (data instanceof CompletableFuture) {
            return ((CompletableFuture<List<T>>) factory.apply(context));
        }

        throw new IllegalArgumentException(
                String.format(
                        "Pagination provider return value must be a List or CompletableFuture (given %s).",
                        data.getClass().getName()));
    }

    public List<T> getPageBlocking(int index) {
        if (source.isEmpty()) return Collections.emptyList();

        int size = size();

        // fast path
        if (size < pageSize) return new ArrayList<>(source);

        if (index < 0 || index >= count())
            throw new ArrayIndexOutOfBoundsException(
                    "Index must be between the range of 0 and "
                            + (count() - 1)
                            + ", given: "
                            + index);

        List<T> page = new LinkedList<>();
        final int base = index * pageSize;
        int until = base + pageSize;
        if (until > size()) until = size;

        for (int i = base; i < until; i++) page.add(get(i));

        return page;
    }

    /**
     * Updates the paging source using the factory or the given source value.
     *
     * @param context The pagination context.
     * @param source The new source value (can be null).
     * @throws IllegalStateException If factory is null.
     */
    void update(PaginatedViewContext<T> context, List<T> source) {
        if (context != null && isProvided()) {
            if (factory == null)
                throw new IllegalStateException("Update cannot be used without a factory");

            this.source = toList(factory.apply(context));
            return;
        }

        this.source = source;
    }

    /**
     * Throws an exception if {@link #source} is null.
     *
     * @throws IllegalStateException If {@link #source} is null.
     */
    private void checkSource() {
        if (source != null) return;
        throw new IllegalStateException(
                String.format(
                        "Paginator source cannot be null (page size = %d, factory = %s, is provided = %b)",
                        pageSize, factory, provided));
    }

    /**
     * Converts a given value to a List. Accepts {@link List}, {@link Collection} and {@link
     * Iterable}.
     *
     * @param value The value.
     * @param <T> The paginator target type.
     * @return The value converted to a List.
     * @throws IllegalArgumentException If unable to convert the value.
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> toList(@NotNull Object value) {
        if (value instanceof List) return (List<T>) value;
        if (value instanceof Collection) return new ArrayList<>((Collection<? extends T>) value);

        if (value instanceof Iterable) {
            final List<T> list = new ArrayList<>();
            for (final T item : (Iterable<? extends T>) value) list.add(item);

            return list;
        }

        throw new IllegalArgumentException(
                String.format(
                        "Failed to convert value to list: %s", value.getClass().getSimpleName()));
    }
}
