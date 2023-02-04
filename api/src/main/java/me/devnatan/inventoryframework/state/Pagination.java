package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public final class Pagination implements State<Pagination> {

	/**
	 * The index-based current page number.
	 *
	 * @param holder The holder whose value will be obtained from.
	 * @return The current page number. {@code 0} will be the first page.
	 */
	public int getCurrentPage(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks for pages before the {@link #getCurrentPage(StateHolder) current} one.
	 *
	 * @param holder The holder whose value will be obtained from.
	 * @return {@code true} if there are previous pages or {@code false} otherwise
	 */
	public boolean hasPreviousPage(@NotNull StateHolder holder) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Checks for pages after the {@link #getCurrentPage(StateHolder) current} one.
	 *
	 * @param holder The holder whose value will be obtained from.
	 * @return {@code true} if there are next pages or {@code false} otherwise
	 */
	public boolean hasNextPage(@NotNull StateHolder holder) {
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
