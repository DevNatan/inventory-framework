package me.saiintbrisson.minecraft.v3;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class BukkitViewContainer implements ViewContainer {

	protected static final int EXPECTED_INVENTORY_SIZE = 9;

	private final List<Player> viewers = new ArrayList<>();

	@NotNull
	abstract Inventory getInventory();

	void addViewer(@NotNull Player player) {
		synchronized (viewers) {
			viewers.add(player);
		}
	}

	void removeViewer(@NotNull Player player) {
	}

	@Override
	public void close() {
		viewers.forEach(Player::closeInventory);
	}

}