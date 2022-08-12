package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IFUtils {

	public static <T> void callIfNotNull(T handler, Consumer<T> fn) {
		if (handler == null) return;
		fn.accept(handler);
	}

	public static String[] useLayout(
		@NotNull VirtualView view,
		@NotNull VirtualView context
	) {
		return context.getLayout() == null ? view.getLayout() : context.getLayout();
	}

	public static <T> Paginator<T> usePaginator(
		@NotNull PaginatedVirtualView<T> view,
		@NotNull PaginatedViewContext<T> context
	) {
		return context.getPaginator() == null ? view.getPaginator() : context.getPaginator();
	}

	public static int useLayoutItemsLayerSize(
		@NotNull Stack<Integer> layoutItemsLayer,
		String[] layout
	) {
		return layout == null ? 0 /* ignored */ : layoutItemsLayer.size();
	}

	public static Stack<Integer> useLayoutItemsLayer(
		@NotNull VirtualView view,
		@NotNull VirtualView context
	) {
		return context.getLayoutItemsLayer() == null ? view.getLayoutItemsLayer() : context.getLayoutItemsLayer();
	}

	public static void checkContainerType(@NotNull ViewContext context) {
		if (context.getContainer().getType() == ViewType.CHEST)
			return;

		throw new IllegalStateException(String.format(
			"Pagination is not supported in \"%s\" view type: %s." + " Use chest type instead.",
			context.getRoot().getType().getIdentifier(), context.getRoot().getClass().getName()
		));
	}

	public static void checkPaginationSourceAvailability(@NotNull ViewContext context) {
		if (context.paginated().getPaginator() != null)
			return;

		throw new IllegalStateException(
			"At least one pagination source must be set. "
				+ "Use #setSource in the PaginatedView constructor or set only to a context"
				+ " in the #onRender(...) function with \"context.paginated().setSource(...)\"."
		);
	}

}
