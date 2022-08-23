package me.saiintbrisson.minecraft;

import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

class Test1 extends View {

    public Test1() {
        slot(9, new ItemStack(Material.GOLD_INGOT)).cancelOnClick().onClick(click -> click.open(Test2.class));

        slot(1).rendered(InventoryFramework::createItem);
        slot(14).cancelOnClick().rendered(InventoryFramework::createItem).referencedBy("abc");
    }

    @Override
    protected void onClick(@NotNull ViewSlotClickContext context) {
        System.out.println("context.isOnEntityContainer() = " + context.isOnEntityContainer());
        System.out.println("context.getContainer() = " + context.getContainer());
        if (context.isOnEntityContainer()) context.setCancelled(true);
    }
}

class Test2 extends View {
    @Override
    protected void onRender(@NotNull ViewContext context) {
        slot(1, 9).rendered(InventoryFramework::createItem);
        context.slot(1, 8).rendered(InventoryFramework::createItem);
    }
}

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

    static ItemStack createItem() {
        return new ItemStack(
                Material.IRON_INGOT, ThreadLocalRandom.current().nextInt(1, Material.IRON_INGOT.getMaxStackSize()));
    }

    public void onEnable() {
        ViewFrame vf = ViewFrame.of(this, new Test1(), new Test2()).register();

        getServer()
                .getPluginManager()
                .registerEvents(
                        new Listener() {
                            @EventHandler
                            void onChat(AsyncPlayerChatEvent e) {
                                vf.open(Test1.class, e.getPlayer());
                            }
                        },
                        this);
    }
}
