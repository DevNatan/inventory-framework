package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class BukkitViewer implements Viewer {

	private final Player player;
	private ViewContainer selfContainer;
	private IFContext context;

	public BukkitViewer(@NotNull Player player, @NotNull IFContext context) {
		this.player = player;
		this.context = context;
	}

	public Player getPlayer() {
		return player;
	}

	@NotNull
	@Override
	public IFContext getContext() {
		return context;
	}

	@Override
	public @NotNull String getId() {
		return getPlayer().getUniqueId().toString();
	}

	@Override
	public void open(@NotNull final ViewContainer container) {
		getPlayer().openInventory(((BukkitViewContainer) container).getInventory());
	}

	@Override
	public void close() {
		getPlayer().closeInventory();
	}

	@Override
	public @NotNull ViewContainer getSelfContainer() {
		if (selfContainer == null)
			selfContainer = new BukkitViewContainer(getPlayer().getInventory(), getContext().isShared(), ViewType.PLAYER);

		return selfContainer;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		BukkitViewer that = (BukkitViewer) o;
		return Objects.equals(getPlayer(), that.getPlayer());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getPlayer());
	}

	@Override
	public String toString() {
		return "BukkitViewer{" + "player=" + player + ", selfContainer=" + selfContainer + '}';
	}
}
