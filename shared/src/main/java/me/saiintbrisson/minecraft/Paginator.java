package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.pagination.IFPaginatedContext;
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
@ToString
public final class Paginator<T> {

    private int pageSize;
    private int pagesCount = -1;
    private List<T> source;
    private Function<IFPaginatedContext<T>, List<T>> factory;
    private AsyncPaginationDataState<T> asyncState;
    private final boolean provided, async;

    Paginator(@NotNull Object source) {
        this(0, source);
    }

    @SuppressWarnings("unchecked")
    Paginator(int pageSize, @NotNull Object source) {
        this.pageSize = pageSize;

        Function<IFPaginatedContext<T>, List<T>> _factory = null;
        List<T> _source = null;
        AsyncPaginationDataState<T> _asyncState = null;

        if (source instanceof List) _source = (List<T>) source;
        else if (source instanceof Function) _factory = (Function<IFPaginatedContext<T>, List<T>>) source;
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

    public boolean isStatic() {
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
        return (int) Math.ceil((double) size() / getPageSize());
    }

    public List<T> getPage(int index) {
        if (source.isEmpty()) return Collections.emptyList();

        int size = size();

        // fast path -- no need to calculate page
        if (size < getPageSize()) return new ArrayList<>(source);

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
}
