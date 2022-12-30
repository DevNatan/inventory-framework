package me.devnatan.inventoryframework.examples.shop;

import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.bukkit.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.state.PaginationState;
import me.devnatan.inventoryframework.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

@RequiredArgsConstructor
public final class ShopListView extends View {

	private final ShopManager shopManager;

	private final PaginationState<Shop> pagination = paginationState(ArrayList::new, this::onItemRender);

	@Override
	protected ViewConfig onInit() {
		return createConfig(6, "Lojas")
			.cancelOnClick()
			.layout(
				"XXXXXXXXX",
				"XOOOOOOOX",
				"XOOOOOOOX",
				"XOOOOOOOX",
				"XOOOOOOOX",
				"XXXXXXXXX"
			);
	}

	private void onItemRender(ViewItem item, String value, IFContext context) {
		context.get context.set


		item.withItem(new ItemStack(Material.SKELETON_SKULL, 1, (short) 3));
	}

}
