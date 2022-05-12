package me.saiintbrisson.minecraft.v3;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
final class BukkitChestViewContainer extends BukkitViewContainer {

	@NotNull
	private final Inventory inventory;

	@Override
	public int getFirstSlot() {
		return 0;
	}

	@Override
	public int getLastSlot() {
		return getSlotsCount() - 1;
	}

	@Override
	public boolean hasItem(int slot) {
		return inventory.getItem(slot) != null;
	}

	@Override
	public int getSlotsCount() {
		return inventory.getSize();
	}

	@Override
	public int getRowSize() {
		return getSlotsCount() / EXPECTED_INVENTORY_SIZE;
	}

}