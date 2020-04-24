package me.saiintbrisson.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {

    private ExampleInventory inventory;

    @Override
    public void onEnable() {
        inventory = new ExampleInventory(this);

        new InventoryFrame(this).registerListener();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void handleInteraction(PlayerInteractEvent event) {
        if(event.getItem() == null) return;
        if(event.getItem().getType() != Material.COMPASS) return;

        Player player = event.getPlayer();
        inventory.createNode(player, player).show();
    }

}
