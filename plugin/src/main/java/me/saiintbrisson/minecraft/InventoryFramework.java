package me.saiintbrisson.minecraft;

import me.saiintbrisson.minecraft.bstats.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {

	private static final String BSTATS_SYSTEM_PROPERTY = "if.enable-bstats";

	@Override
	public void onEnable() {
		try {
			if (Boolean.parseBoolean(
				System.getProperty(BSTATS_SYSTEM_PROPERTY, Boolean.TRUE.toString())
			)) new Metrics(this, 15518);
		} catch (final Exception ignored) {
		}
	}

}