package me.devnatan.inventoryframework.bukkit;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
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

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.size(3).layout("  FFFFF  ", " AAAAAAA ", "  FFFFF  ");
    }

    @Override
    public void onFirstRender(RenderContext render) {
        render.layoutSlot('A', new ItemStack(Material.GOLD_INGOT));
        render.layoutSlot('F', new ItemStack(Material.DIAMOND));
    }
}
