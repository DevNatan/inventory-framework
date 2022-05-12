package me.saiintbrisson.minecraft.v3;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ToString
@RequiredArgsConstructor
class BukkitViewer implements Viewer {

	@NotNull
	private final Player player;

	@Override
	public void close() {
		player.closeInventory();
	}

}