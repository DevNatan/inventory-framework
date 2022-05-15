package me.saiintbrisson.minecraft;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform View backward compatible implementation.
 */
@Getter
public class View extends AbstractView implements InventoryHolder {

	public View() {
		this(0);
	}

	public View(int rows) {
		this(rows, null);
	}

	public View(int rows, String title) {
		this(rows, title, ViewType.CHEST);
	}

	public View(int rows, String title, @NotNull  ViewType type) {
		super(rows, title, type);
	}

	@Override
	public final void open(
		@NotNull Object player,
		@NotNull Map<String, Object> data
	) {
		open(getViewFrame().getFactory().createViewer(player), data);
	}

	@SuppressWarnings("ConstantConditions")
	@NotNull
	@Override
	public final Inventory getInventory() {
		return null;
	}

}
