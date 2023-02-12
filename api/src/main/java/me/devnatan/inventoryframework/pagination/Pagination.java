package me.devnatan.inventoryframework.pagination;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentComposition;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateHolder;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public final class Pagination implements State<Pagination>, ComponentComposition {

	private final RootView root;

	@Override
	public @NotNull VirtualView getRoot() {
		return root;
	}

	@Override
	public int getPosition() {
		return 0;
	}

	@Override
	public Component[] getComponents() {
		return new Component[0];
	}

	/**
	 * The index-based current page number.
	 *
	 * @param context The context whose value will be obtained from.
	 * @return The current page number. {@code 0} will be the first page.
	 */
	public int getCurrentPage(@NotNull IFContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks for pages before the current page.
	 *
	 * @param context The context whose value will be obtained from.
	 * @return {@code true} if there are previous pages or {@code false} otherwise
	 */
	public boolean hasPreviousPage(@NotNull IFContext context) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks for pages after the current page.
	 *
	 * @param context The context whose value will be obtained from.
	 * @return {@code true} if there are next pages or {@code false} otherwise
	 */
	public boolean hasNextPage(@NotNull IFContext context) {
		throw new UnsupportedOperationException();
	}

	public boolean isFirstPage(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	public boolean isLastPage(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	public int count(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	public void back(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	public void advance(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	public boolean canBack(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	public boolean canAdvance(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Pagination get(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}
}
