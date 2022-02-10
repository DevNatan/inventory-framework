package me.saiintbrisson.minecraft;

import org.bukkit.plugin.Plugin;

public interface ViewProvider {

	Plugin getHolder();

	ViewFrame getFrame();

}
