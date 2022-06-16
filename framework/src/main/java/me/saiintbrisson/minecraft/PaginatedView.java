package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform PaginatedView backward compatible implementation.
 */
public abstract class PaginatedView<T> extends BasePaginatedView<T> {

	public PaginatedView() {
		this(0);
	}

	public PaginatedView(int size) {
		this(size, null);
	}

	public PaginatedView(int size, String title) {
		this(size, title, ViewType.CHEST);
	}

	public PaginatedView(int size, String title, @NotNull ViewType type) {
		super(size, title, type);
	}

	@Override
	public final int getPageSize() {
		return super.getPageSize();
	}

}