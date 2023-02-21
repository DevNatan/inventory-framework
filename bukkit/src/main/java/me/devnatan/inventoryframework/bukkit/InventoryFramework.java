package me.devnatan.inventoryframework.bukkit;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.pagination.Pagination;
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
                                vf.open(AwesomeView.class, event.getPlayer());
                            }
                        },
                        this);
    }
}

class AwesomeView extends View {

    private final State<Pagination> pagination = pagination(
            () -> IntStream.range(1, 100).boxed().collect(Collectors.toList()),
            (item, value) -> item.item(new ItemStack(Material.EGG)));

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title("Awesome view")
                .size(6)
                .cancelOnClick()
                .layout("XXXXXXXXX", "XOOOOOOOX", "XOOOOOOOX", "XOOOOOOOX", "XOOOOOOOX", "XXXXXXXXX");
    }

    @Override
    public void onFirstRender(RenderContext ctx) {
        ctx.slot(2, new ItemStack(Material.EGG))
                .cancelOnClick()
                .onUpdate(update -> update.getPlayer().sendMessage("Item update called"))
                .onClick(click -> {
                    click.getPlayer().sendMessage("Clicked update");
                    click.update();
                });

        ctx.slot(
                3, () -> new ItemStack(Material.EGG, ThreadLocalRandom.current().nextInt(1, 17)));
    }

    @Override
    public void onUpdate(Context ctx) {
        System.out.println("Global update called: " + ctx);
    }

    @Override
    public void onClick(SlotClickContext ctx) {
        System.out.println("Global click called: " + ctx);
    }
}
