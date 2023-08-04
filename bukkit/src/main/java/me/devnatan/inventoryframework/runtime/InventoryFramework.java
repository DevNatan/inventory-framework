package me.devnatan.inventoryframework.runtime;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    public static final String LIBRARY_VERSION = "3.0.0-alpha";

    @Override
    public void onEnable() {
        ViewFrame vf = ViewFrame.create(this).with(new TestView()).register();
        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            public void onChat(AsyncPlayerChatEvent e) {
                                getServer().getScheduler().runTask(InventoryFramework.this, () -> {
                                    vf.open(TestView.class, Bukkit.getOnlinePlayers());
                                });
                            }
                        },
                        this);

        getServer().getOnlinePlayers().forEach(Player::closeInventory);
    }
}

class TestView extends View {

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.scheduleUpdate(20L);
    }

    @Override
    public void onFirstRender(RenderContext render) {
        render.firstSlot(new ItemStack(Material.GOLD_INGOT));
    }

    @Override
    public void onOpen(OpenContext open) {
        for (final Player player : open.getAllPlayers()) {
            player.sendMessage(String.format("Todos que estão vendo: "
                    + open.getAllPlayers().stream().map(Player::getName).collect(Collectors.joining(", "))));
        }
    }

    @Override
    public void onUpdate(Context update) {
        update.updateTitle(String.valueOf(ThreadLocalRandom.current().nextInt()));
    }

    @Override
    public void onClose(CloseContext close) {
        for (final Player player : close.getAllPlayers()) {
            player.sendMessage(String.format("%s fechou o inventário", close.getPlayer()));
        }
    }
}
