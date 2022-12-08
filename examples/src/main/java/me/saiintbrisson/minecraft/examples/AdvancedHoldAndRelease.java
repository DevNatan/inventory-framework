package me.saiintbrisson.minecraft.examples;

import me.devnatan.inventoryframework.config.ViewConfig;
import me.devnatan.inventoryframework.state.State;
import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.ViewContext;
import me.saiintbrisson.minecraft.ViewSlotContext;
import me.saiintbrisson.minecraft.ViewSlotMoveContext;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public final class AdvancedHoldAndRelease extends View {

	private static final int DEFAULT_POSITION = 22;

	private final State<Integer> position = state(() -> DEFAULT_POSITION);

	@Override
	protected ViewConfig configure() {
		return ViewConfig.create().title("Take an item").size(3);
	}

    @Override
    protected void onRender(@NotNull ViewContext context) {
		context.slot(position.get(), new ItemStack(Material.PAPER));
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

		position.update(fromContext, toContext.getSlot());

		final Player player = fromContext.getPlayer();
		player.playSound(player.getLocation(), "random.click", 1F, 1F);
    }

    @Override
    protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
        context.setCancelled(true);
        context.getPlayer().sendMessage("You can't move items out!");
    }
}
