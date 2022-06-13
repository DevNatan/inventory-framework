package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform PaginatedView backward compatible implementation.
 */
public abstract class PaginatedView<T> extends BasePaginatedView<T> {

	public PaginatedView() {
		this(0);
	}

	public PaginatedView(int rows) {
		this(rows, null);
	}

	public PaginatedView(int rows, String title) {
		this(rows, title, ViewType.CHEST);
	}

	public PaginatedView(int rows, String title, @NotNull ViewType type) {
		super(rows, title, type);
	}

	@Override
	public final int getPageSize() {
		return super.getPageSize();
	}

}