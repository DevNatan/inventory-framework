package me.devnatan.inventoryframework.state;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface Pagination extends State<List<Object>> {

    /**
     * The index-based current page number.
     *
     * @param holder The holder whose value will be obtained from.
     * @return The current page number. {@code 0} will be the first page.
     */
    int getCurrentPage(@NotNull StateHolder holder);

    /**
     * Checks for pages before the {@link #getCurrentPage(StateHolder) current} one.
     *
     * @param holder The holder whose value will be obtained from.
     * @return {@code true} if there are previous pages or {@code false} otherwise
     */
    boolean hasPreviousPage(@NotNull StateHolder holder);

    /**
     * Checks for pages after the {@link #getCurrentPage(StateHolder) current} one.
     *
     * @param holder The holder whose value will be obtained from.
     * @return {@code true} if there are next pages or {@code false} otherwise
     */
    boolean hasNextPage(@NotNull StateHolder holder);

    boolean isFirstPage(@NotNull StateHolder holder);

    boolean isLastPage(@NotNull StateHolder holder);

    int count(@NotNull StateHolder holder);

    void back(@NotNull StateHolder holder);

    void advance(@NotNull StateHolder holder);

    boolean canBack(@NotNull StateHolder holder);

    boolean canAdvance(@NotNull StateHolder holder);
}
