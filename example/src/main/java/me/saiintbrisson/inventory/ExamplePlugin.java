package me.saiintbrisson.inventory;

import me.saiintbrisson.inventory.InventoryFrame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        super.onEnable();

        new InventoryFrame(this).registerListener();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void handleInteraction(PlayerInteractEvent event) {
        if(event.getItem() == null) return;
        if(event.getItem().getType() != Material.COMPASS) return;

         new me.saiintbrisson.inventory.example.ExampleInventory().createNode(event.getPlayer()).show(event.getPlayer());
    }

}
