package me.saiintbrisson.minecraft;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

class TestView extends View {

	public TestView() {
		super(3, "Test");
		slot(1, new ItemStack(Material.GOLDEN_APPLE))
			.cancelOnClick()
			.onClick(click -> click.getPlayer().sendMessage("Ola mundo"));
	}

	@Override
	protected void onClick(@NotNull ViewSlotContext context) {
		context.getPlayer().sendMessage("Global click");
	}

}

public final class InventoryFramework extends JavaPlugin {

	public void onEnable() {
		ViewFrame vf = new ViewFrame(this);
		vf.with(new TestView());
		vf.register();

		getServer().getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onChat(AsyncPlayerChatEvent e) {
				vf.open(TestView.class, e.getPlayer());
			}
		}, this);
	}

}
