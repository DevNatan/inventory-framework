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
@RequiredArgsConstructor
public class View extends AbstractView implements InventoryHolder {

	private final int rows;
	private final String title;

	public View() {
		this(0);
	}

	public View(int rows) {
		this(rows, null);
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
