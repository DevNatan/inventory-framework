package me.saiintbrisson.minecraft;

import lombok.Getter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
@Getter
public class View extends AbstractView implements ItemFactory<ItemStack>, InventoryHolder {

	public View() {
		this(0);
	}

	public View(int rows) {
		this(rows, null);
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
	public ViewItem item(@NotNull ItemStack stack) {
		// TODO remove it. XD :)
		final ViewItem item = new ViewItem();
		item.setItem(stack);
		return item;
	}

}
