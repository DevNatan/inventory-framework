package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.PaginatedView;
import me.saiintbrisson.minecraft.PaginatedViewSlotContext;
import me.saiintbrisson.minecraft.ViewItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

public final class PersistentPaginatedView extends PaginatedView<Integer> {

	public PersistentPaginatedView() {
		super(6, "Paginated view");

		// pagination source
		setSource(IntStream.rangeClosed(0, 53).boxed().collect(Collectors.toList()));
	}

	@Override
	protected void onItemRender(PaginatedViewSlotContext<Integer> render, ViewItem item, Integer value) {
		item.withItem(createPaginationItemStack(value));
	}

	private ItemStack createPaginationItemStack(int value) {
		ItemStack stack = new ItemStack(Material.PAPER);
		ItemMeta meta = requireNonNull(stack.getItemMeta());
		meta.setDisplayName("Item " + value);
		stack.setItemMeta(meta);
		return stack;
	}

}
