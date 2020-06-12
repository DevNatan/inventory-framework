package me.saiintbrisson.inventory.example;

import me.saiintbrisson.inventory.InventoryFrame;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin implements Listener {

    private ExampleGUI gui;

    @Override
    public void onEnable() {
        gui = new ExampleGUI(this);

        new InventoryFrame(this).registerListener();
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void handleInteraction(PlayerInteractEvent event) {
        if(event.getItem() == null) return;
        if(event.getItem().getType() != Material.COMPASS) return;

        Player player = event.getPlayer();
        gui.createNode(
          player,
          new ExampleObject("Luiz Carlos", "luizcarlosmpc@gmail.com", 16)
        ).show();
    }

}
