package me.saiintbrisson.minecraft;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryPlugin extends JavaPlugin implements Listener {

    private ExampleView view;

    @Override
    public void onEnable() {
        view = new ExampleView(this);

        new InventoryFrame(this).registerListener();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void handleInteraction(PlayerInteractEvent event) {
        if(event.getItem() == null) return;
        if(event.getItem().getType() != Material.COMPASS) return;

        view.createNode(
          new ExampleObject("Luiz Carlos", "luizcarlosmpc@gmail.com", 16)
        ).show(event.getPlayer());
    }

}
