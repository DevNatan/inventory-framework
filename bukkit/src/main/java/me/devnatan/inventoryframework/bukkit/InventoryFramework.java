package me.devnatan.inventoryframework.bukkit;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.component.Pagination;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                                vf.open(AwesomeView.class, event.getPlayer());
                            }
                        },
                        this);
    }
}

class AwesomeView extends View {

	private final State<Pagination> pagination = pagination(
		() -> IntStream.rangeClosed(1, 100).boxed().collect(Collectors.toList()),
		(item, value) -> item.withItem(new ItemStack(Material.PAPER))
	);

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title("Awesome view").size(6).cancelOnClick();
    }

    @Override
    public void onFirstRender(RenderContext ctx) {
        ctx.availableSlot(() -> new ItemStack(Material.EGG))
                .cancelOnClick()
                .onUpdate(update -> update.getPlayer().sendMessage("Item update called"))
                .onClick(click -> {
                    click.getPlayer().sendMessage("Clicked update");
                    ctx.update();
                });
    }
}
