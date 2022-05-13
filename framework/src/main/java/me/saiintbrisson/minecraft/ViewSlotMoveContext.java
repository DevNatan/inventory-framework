package me.saiintbrisson.minecraft;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface ViewSlotMoveContext extends ViewSlotContext {

	@Deprecated
	Inventory getTargetInventory();

	@Deprecated
	ItemStack getTargetItem();

	@Deprecated
	default ItemStack getSwappedItem() {
		if (!isSwap()) throw new IllegalStateException("not swap");
		return getTargetItem();
	}

	int getTargetSlot();

	boolean isSwap();

	boolean isStack();

}
