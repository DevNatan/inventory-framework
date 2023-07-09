package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.0-EAP";

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).with(new TestView()).register();

        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            public void onChat(AsyncPlayerChatEvent e) {
                                vf.open(TestView.class, e.getPlayer());
                            }
                        },
                        this);
    }
}

class TestView extends View {

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.layout("OOOOOOOOO");
    }

    @Override
    public void onFirstRender(RenderContext render) {}
}
