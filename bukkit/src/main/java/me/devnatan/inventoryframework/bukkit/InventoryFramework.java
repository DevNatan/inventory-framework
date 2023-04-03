package me.devnatan.inventoryframework.bukkit;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.MutableIntState;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).with(new AwesomeView()).register();

        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            void onChat(AsyncPlayerChatEvent event) {
                                getServer().getScheduler().runTask(InventoryFramework.this, () -> {
                                    vf.open(AwesomeView.class, event.getPlayer());
                                });
                            }
                        },
                        this);
    }
}

class AwesomeView extends View {

	private final MutableIntState amountState = mutableIntState(1);

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title("Amount selector").cancelOnClick();
    }

	@Override
	public void onFirstRender(RenderContext render) {
		render.slot(2, 3, new ItemStack(Material.ARROW))
			.onClick(click -> {
			amountState.decrement(render);
			click.updateRoot();
		}).create();
		render.slot(2, 7, new ItemStack(Material.ARROW)).onClick(click -> {
			amountState.increment(render);
			click.updateRoot();
		});
		render.slot(2, 5).watch(amountState).onRender(slotRender -> {
			final int newValue = amountState.get(render);
			System.out.println("re-rendered item with " + newValue);
			slotRender.setItem(new ItemStack(Material.GOLD_INGOT, newValue));
		});
	}
}
