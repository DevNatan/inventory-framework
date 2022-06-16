package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class BukkitViewSlotContext extends AbstractViewSlotContext {

	BukkitViewSlotContext(ViewItem backingItem, @NotNull BaseViewContext parent) {
		super(backingItem, parent);
	}

	@Override
	public Player getPlayer() {
		return BukkitViewer.toPlayerOfContext(this);
	}

}
