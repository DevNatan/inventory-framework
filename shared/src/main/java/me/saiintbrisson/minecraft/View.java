package me.saiintbrisson.minecraft;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
public class View extends AbstractView implements InventoryHolder {

	public View() {
		this(0);
	}

	public View(int size) {
		this(size, null);
	}

	public View(String title) {
		this(0, title);
	}

	@ApiStatus.Experimental
	public View(String title, @NotNull ViewType type) {
		this(0, title, type);
	}

	@ApiStatus.Experimental
	public View(@NotNull ViewType type) {
		this(0, null, type);
	}

	public View(int size, String title) {
		this(size, title, ViewType.CHEST);
	}

	@ApiStatus.Experimental
	public View(int size, String title, @NotNull ViewType type) {
		super(size, title, type);
	}

	@NotNull
	@Override
	public final Inventory getInventory() {
		throw new IllegalStateException("View inventory cannot be accessed");
	}

	@Override
	public final String toString() {
		return super.toString();
	}
}
