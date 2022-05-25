package me.saiintbrisson.minecraft.examples;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewSlotContext;
import me.saiintbrisson.minecraft.ViewSlotMoveContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class HoldAndRelease extends View {

	public HoldAndRelease() {
		super(3, "Hold and Release");
		firstSlot(new ItemStack(Material.PAPER));
	}

	@Override
	protected void onItemHold(@NotNull ViewSlotContext context) {
		context.updateTitle("Release the item");
		context.getPlayer().playSound(context.getPlayer().getLocation(), "random.click", 1F, 1F);
	}

	@Override
	protected void onItemRelease(@NotNull ViewSlotContext fromContext, @NotNull ViewSlotContext toContext) {
		fromContext.resetTitle();
		fromContext.getPlayer().playSound(fromContext.getPlayer().getLocation(), "random.click", 1F, 1F);
	}

}
