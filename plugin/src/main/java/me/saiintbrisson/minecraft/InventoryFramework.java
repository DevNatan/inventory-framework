package me.saiintbrisson.minecraft;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static me.saiintbrisson.minecraft.MoveInOutFeature.MoveInOut;

class TestView extends View {

	public TestView() {
		super(3, "Test");
		slot(0, new ItemStack(Material.GOLDEN_APPLE))
			.cancelOnClick()
			.onClick(click -> click.getPlayer().sendMessage("Ola mundo 1"));

		slot(1, new ItemStack(Material.GOLDEN_APPLE))
			.onClick(click -> {
				click.setCancelled(true);
				click.getPlayer().sendMessage("Ola mundo 2");
			});

		slot(2, new ItemStack(Material.GOLDEN_APPLE))
			.onClick(click -> click.getPlayer().sendMessage("Ola mundo 3"));

		slot(3, new ItemStack(Material.GOLDEN_APPLE))
			.onClick(click -> click.getPlayer().sendMessage("Ola mundo 4"));

		slot(4, new ItemStack(Material.GOLDEN_APPLE))
			.cancelOnClick()
			.onClick(click -> {
				click.setCancelled(false);
				click.getPlayer().sendMessage("Ola mundo 5");
			});

		slot(7, new org.bukkit.inventory.ItemStack(org.bukkit.Material.GOLDEN_APPLE)).referencedBy("test");

		slot(8).cancelOnClick()
			.onRender(render -> render.setItem(new ItemStack(Material.values()[new Random().nextInt(Material.values().length)])))
			.onClick(click -> click.setItem(new ItemStack(Material.GOLDEN_APPLE)));
	}

	@Override
	protected void onClick(@NotNull ViewSlotContext context) {
		context.getPlayer().sendMessage("Global clic at " + context.getSlot() +" player inventory: " + context.isOnEntityContainer());

		context.getPlayer().sendMessage("test: " + context.ref("test").getSlot());

		if (context.getSlot() == 3) {
			context.getPlayer().sendMessage("cancel");
			context.setCancelled(true);
		}
	}

	@Override
	protected void onMoveOut(@NotNull ViewSlotMoveContext context) {
		context.getPlayer().sendMessage("moved out");
	}

}

public final class InventoryFramework extends JavaPlugin {

	public void onEnable() {
		ViewFrame vf = new ViewFrame(this);
		vf.install(MoveInOut);
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
