package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public interface CompatViewFrame<T extends CompatViewFrame<T>>
	extends PlatformViewFrame<Player, Plugin, T> {

	AbstractView get(@NotNull Player player);

}
