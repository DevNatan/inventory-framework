package me.saiintbrisson.minecraft;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform PaginatedView backward compatible implementation.
 */
public abstract class PaginatedView<T> extends AbstractPaginatedView<T> implements InventoryHolder {

	public PaginatedView() {
		this(0);
	}

	public PaginatedView(int size) {
		this(size, null);
	}

	public PaginatedView(String title) {
		this(0, title);
	}

	@ApiStatus.Experimental
	public PaginatedView(String title, @NotNull ViewType type) {
		this(0, title, type);
	}

	@ApiStatus.Experimental
	public PaginatedView(@NotNull ViewType type) {
		this(0, null, type);
	}

	public PaginatedView(int size, String title) {
		this(size, title, ViewType.CHEST);
	}

	@ApiStatus.Experimental
	public PaginatedView(int size, String title, @NotNull ViewType type) {
		super(size, title, type);
	}

	@NotNull
	@Override
	public final Inventory getInventory() {
		throw new IllegalStateException("View inventory cannot be accessed");
	}

}