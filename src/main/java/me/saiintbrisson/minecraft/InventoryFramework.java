package me.saiintbrisson.minecraft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class InventoryFramework extends JavaPlugin {

	/*
	/~~\
	 */
	private static final class TestView extends View {

		public TestView() {
			super(6, "Test view");

			slot(0, new ItemStack(Material.AIR));
			slot(46, new ItemStack(Material.DIAMOND));
			slot(6, 3, new ItemStack(Material.GOLD_INGOT));
		}

		@Override
		protected void onMoveIn(@NotNull ViewSlotMoveContext context) {
			context.getPlayer().sendMessage("Move In to " + context.getTargetSlot());
		}

		@Override
		protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
			context.getPlayer().sendMessage("Move Out");
		}

	}

	@Override
	public void onEnable() {
		final ViewFrame viewFrame = new ViewFrame(this);
		viewFrame.register(new TestView());
		getCommand("if").setExecutor((sender, $, $$, $$$) -> {
			viewFrame.open(TestView.class, (Player) sender);
			return true;
		});
	}

}
