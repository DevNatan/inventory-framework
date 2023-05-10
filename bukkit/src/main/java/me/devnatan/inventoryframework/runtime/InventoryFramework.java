package me.devnatan.inventoryframework.runtime;

import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.context.RenderContext;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
        ViewFrame vf = ViewFrame.create(this).with(new Pirocoptero()).register();
        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            public void onChat(AsyncPlayerChatEvent event) {
                                getServer().getScheduler().runTask(InventoryFramework.this, () -> {
                                    vf.open(Pirocoptero.class, event.getPlayer());
                                });
                            }
                        },
                        this);
    }
}

class Pirocoptero extends View {

    @Override
    public void onInit(ViewConfigBuilder config) {
        config.title(Component.text("piroca de foice", TextColor.color(255, 0, 255)));
    }

    @Override
    public void onFirstRender(RenderContext render) {
        render.firstSlot(new ItemStack(Material.DIAMOND)).onClick(click -> click.updateTitle("abc da xuxa"));
    }
}
