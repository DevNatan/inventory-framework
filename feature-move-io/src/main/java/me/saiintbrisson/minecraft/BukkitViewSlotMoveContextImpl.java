package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString(callSuper = true)
final class BukkitViewSlotMoveContextImpl extends BukkitClickViewSlotContext implements ViewSlotMoveContext {

	@ToString.Exclude
	private final ViewContainer targetContainer;

	private final Object targetItem, swappedItem;
	private final int targetSlot;
	private final boolean swap, stack;

	BukkitViewSlotMoveContextImpl(@NotNull ViewContext parent,
								  @NotNull InventoryClickEvent clickOrigin,
								  ViewContainer targetContainer,
								  Object targetItem,
								  Object swappedItem,
								  int targetSlot,
								  boolean swap,
								  boolean stack) {
		super(parent, clickOrigin);
		this.targetContainer = targetContainer;
		this.targetItem = targetItem;
		this.swappedItem = swappedItem;
		this.targetSlot = targetSlot;
		this.swap = swap;
		this.stack = stack;
	}

}
