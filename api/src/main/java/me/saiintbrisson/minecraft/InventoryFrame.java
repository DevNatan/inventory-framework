package me.saiintbrisson.minecraft;

import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@AllArgsConstructor
public class InventoryFrame {

    private final Plugin owner;

    public void registerListener() {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(owner), owner);
    }

}
