package me.eotaldo;

import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.state.Pagination;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class TestView extends View {

	private final Pagination<String> pagination = pagination(this::buildPaginationItem);

	@Override
	public void onInit(ViewConfigBuilder config) {

	}

	private void buildPaginationItem(IFItem item, String value) {
		item.withItem(new ItemStack(Material.GOLD_INGOT));
	}

}