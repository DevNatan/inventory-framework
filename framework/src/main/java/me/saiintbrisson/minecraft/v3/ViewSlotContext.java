package me.saiintbrisson.minecraft.v3;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public interface ViewSlotContext extends ViewContext {

	@Deprecated
	InventoryClickEvent getClickOrigin();



	boolean isCancelled();

	void setCancelled(boolean cancelled);

}
