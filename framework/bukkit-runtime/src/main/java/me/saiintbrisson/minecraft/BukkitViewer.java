package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@ToString
@Getter
@RequiredArgsConstructor
class BukkitViewer implements Viewer {

	@NotNull
	private final Player player;

	@Override
	public void open(@NotNull final ViewContainer container) {
		if (!(container instanceof BukkitViewContainer))
			throw new IllegalArgumentException("Only BukkitViewContainer is supported");

		container.open(this);
		player.openInventory(((BukkitViewContainer) container).getInventory());
	}

	@Override
	public void close() {
		player.closeInventory();
	}

}