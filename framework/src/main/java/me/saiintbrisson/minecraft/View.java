package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
@Getter
@ToString(callSuper = true)
public class View extends AbstractView implements ItemFactory<ItemStack>, InventoryHolder {

	public View() {
		this(0);
	}

	public View(int rows) {
		this(rows, null);
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

	public View(int rows, String title) {
		this(rows, title, ViewType.CHEST);
	}

	public View(int rows, String title, @NotNull ViewType type) {
		super(rows, title, type);
	}

	@SuppressWarnings("ConstantConditions")
	@NotNull
	@Override
	public final Inventory getInventory() {
		return null;
	}

	@Override
	public final ViewItem item(@NotNull ItemStack stack) {
		// TODO remove it. XD :)
		final ViewItem item = new ViewItem();
		item.setItem(stack);
		return item;
	}

}
