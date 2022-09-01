package me.saiintbrisson.minecraft;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

class MyView extends View {

    public MyView() {
        scheduleUpdate(20);
    }

    @Override
    protected void onUpdate(@NotNull ViewContext context) {
        context.getPlayer().sendMessage("update");
    }
}

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.of(this, new MyView()).register();
        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            public void onChat(AsyncPlayerChatEvent e) {
                                vf.open(MyView.class, e.getPlayer());
                            }
                        },
                        this);
    }
}
