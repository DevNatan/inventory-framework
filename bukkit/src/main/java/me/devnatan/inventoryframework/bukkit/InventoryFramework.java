package me.devnatan.inventoryframework.bukkit;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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

class AwesomeView extends View {}
