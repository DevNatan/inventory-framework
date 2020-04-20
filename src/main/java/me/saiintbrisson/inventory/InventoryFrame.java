package me.saiintbrisson.inventory;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public class InventoryFrame {

    private Plugin owner;

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(owner), owner);
    }

}
