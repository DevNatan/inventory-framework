package me.devnatan.inventoryframework.bukkit;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.state.MutableState;
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

    private final MutableState<Integer> counter = mutableState(3);

    @Override
    public void onFirstRender(RenderContext render) {
        render.availableSlot()
                .onRender(slotRender -> slotRender.setItem(new ItemStack(Material.DIAMOND, counter.get(render))))
                .onClick(click -> {
                    click.getPlayer().sendMessage("clicked");
                    counter.set(counter.get(render) + 1, render);
                });
    }
}
