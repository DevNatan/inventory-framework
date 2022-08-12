package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
public final class Paginator<T> {

    private int pageSize;
	private int pagesCount = -1;
    private List<T> source;
    private Function<PaginatedViewContext<T>, List<T>> factory;
    private AsyncPaginationDataState<T> asyncState;
    private final boolean provided, async;

    @SuppressWarnings("unchecked")
    Paginator(int pageSize, @NotNull Object source) {
        this.pageSize = pageSize;

        Function<PaginatedViewContext<T>, List<T>> _factory = null;
        List<T> _source = null;
        AsyncPaginationDataState<T> _asyncState = null;

        if (source instanceof List) _source = (List<T>) source;
        else if (source instanceof Function) _factory = (Function<PaginatedViewContext<T>, List<T>>) source;
        else if (source instanceof AsyncPaginationDataState) _asyncState = (AsyncPaginationDataState<T>) source;
        else
            throw new IllegalArgumentException(
                    "Unsupported pagination source type: " + source.getClass().getName());

        this.factory = _factory;
        this.source = _source;
        this.asyncState = _asyncState;
        this.provided = _factory != null;
        this.async = _asyncState != null;
    }

    public boolean isSync() {
        return !async && !provided;
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
     * Number of pages.
     *
     * @return The number of pages.
     * @throws IllegalStateException If source is null.
     */
    public int count() {
        checkSource();
        return isSync()
                ? (int) Math.ceil((double) size() / pageSize)
                : pagesCount == -1 ? Integer.MAX_VALUE - 1 : pagesCount;
    }

    public List<T> getPage(int index) {
        if (source.isEmpty()) return Collections.emptyList();

        // fast path -- non-sync pagination source is always updated
        if (!isSync()) return source;

        int size = size();

        // fast path -- no need to calculate page
        if (size < pageSize) return new ArrayList<>(source);

        if (index < 0 || index >= count())
            throw new ArrayIndexOutOfBoundsException(
                    "Index must be between the range of 0 and " + (count() - 1) + ", given: " + index);

        List<T> page = new LinkedList<>();
        final int base = index * pageSize;
        int until = base + pageSize;
        if (until > size()) until = size;

        for (int i = base; i < until; i++) page.add(get(i));

        return page;
    }

    /**
     * Throws an exception if {@link #source} is null.
     *
     * @throws IllegalStateException If {@link #source} is null.
     */
    private void checkSource() {
        if (source != null) return;
        throw new IllegalStateException(String.format(
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

        throw new IllegalArgumentException(String.format(
                "Failed to convert value to list: %s", value.getClass().getSimpleName()));
    }
}
