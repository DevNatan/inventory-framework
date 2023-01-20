package me.eotaldo;

import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewItem;
import me.devnatan.inventoryframework.bukkit.View;
import me.devnatan.inventoryframework.state.Pagination;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class TestView extends View {

	private final Pagination<String> pagination = pagination(this::buildPaginationItem);

	@Override
	public void onInit(ViewConfigBuilder config) {

	}

	private void buildPaginationItem(ViewItem item, String value) {
		item.withItem(new ItemStack(Material.GOLD_INGOT));
	}

}