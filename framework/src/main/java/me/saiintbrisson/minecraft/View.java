package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
@Getter
@ToString(callSuper = true)
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

	public View(String title, @NotNull ViewType type) {
		this(0, title, type);
	}

	public View(@NotNull ViewType type) {
		this(0, null, type);
	}

	public View(int size, String title) {
		this(size, title, ViewType.CHEST);
	}

	public View(int size, String title, @NotNull ViewType type) {
		super(size, title, type);
	}

	@SuppressWarnings("ConstantConditions")
	@NotNull
	@Override
	public final Inventory getInventory() {
		return null;
	}

}
