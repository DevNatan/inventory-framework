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

	private final Map<UUID, Integer> position = new HashMap<>();

	public HoldAndRelease() {
		super(3, "Take an item");
	}

	@Override
	protected void onRender(@NotNull ViewContext context) {
		context.slot(
			position.getOrDefault(context.getPlayer().getUniqueId(), 22),
			new ItemStack(Material.PAPER)
		);
	}

	@Override
	protected void onItemHold(@NotNull ViewSlotContext context) {
		context.updateTitle("Release the item");
		context.getPlayer().playSound(context.getPlayer().getLocation(), "random.click", 1F, 1F);
	}

	@Override
	protected void onItemRelease(@NotNull ViewSlotContext fromContext, @NotNull ViewSlotContext toContext) {
		fromContext.resetTitle();

		// the player held the item and released it into the same slot
		if (fromContext.getSlot() == toContext.getSlot())
			return;

		fromContext.getPlayer().playSound(fromContext.getPlayer().getLocation(), "random.click", 1F, 1F);
		position.put(fromContext.getPlayer().getUniqueId(), toContext.getSlot());
	}

	@Override
	protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
		context.setCancelled(true);
		context.getPlayer().sendMessage("You can't move items out!");
	}

}
