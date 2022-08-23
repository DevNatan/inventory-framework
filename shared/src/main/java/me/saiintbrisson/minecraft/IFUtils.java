package me.saiintbrisson.minecraft;

import java.util.Stack;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IFUtils {

    public static <T> void callIfNotNull(T handler, Consumer<T> fn) {
        if (handler == null) return;
        fn.accept(handler);
    }

    public static <T> T elvis(T input, T fallback) {
        return input == null ? fallback : input;
    }

    public static int useLayoutItemsLayerSize(@NotNull Stack<Integer> layoutItemsLayer, String[] layout) {
        return layout == null ? 0 /* ignored */ : layoutItemsLayer.size();
    }

    public static Stack<Integer> useLayoutItemsLayer(@NotNull VirtualView view, @NotNull VirtualView context) {
        return context.getLayoutItemsLayer() == null ? view.getLayoutItemsLayer() : context.getLayoutItemsLayer();
    }

    public static void checkContainerType(@NotNull ViewContext context) {
        if (context.getContainer().getType() == ViewType.CHEST) return;

        throw new IllegalStateException(String.format(
                "Pagination is not supported in \"%s\" view type: %s." + " Use chest type instead.",
                context.getRoot().getType().getIdentifier(),
                context.getRoot().getClass().getName()));
    }

    public static void checkPaginationSourceAvailability(@NotNull ViewContext context) {
        PaginatedViewContext<?> paginatedContext = context.paginated();
        if (paginatedContext.getRoot().getPaginator() != null || paginatedContext.getPaginator() != null) return;

        throw new IllegalStateException("At least one pagination source must be set. "
                + "Use #setSource in the PaginatedView constructor or set only to a context"
                + " in the #onRender(...) function with \"context.paginated().setSource(...)\".");
    }

    public static int convertSlot(int row, int column, int maxRowsCount, int maxColumnsCount) {
        if (row > maxRowsCount)
            throw new IllegalArgumentException(
                    String.format("Row cannot be greater than %d (given %d)", maxRowsCount, row));

        if (column > maxColumnsCount)
            throw new IllegalArgumentException(
                    String.format("Column cannot be greater than %d (given %d)", maxColumnsCount, column));

        return Math.max(row - 1, 0) * maxColumnsCount + Math.max(column - 1, 0);
    }

    static Object unwrap(Object item) {
        if (item instanceof ItemWrapper) return unwrap(((ItemWrapper) item).getValue());

        return item;
    }
}
