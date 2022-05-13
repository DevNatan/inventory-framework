package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface CompatViewFrame<T extends CompatViewFrame<T>> extends PlatformViewFrame<Player, Plugin, T> {
}
