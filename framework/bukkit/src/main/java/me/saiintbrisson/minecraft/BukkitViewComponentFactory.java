package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class BukkitViewComponentFactory implements ViewComponentFactory {

	private Boolean worksInCurrentPlatform = null;

	@Override
	public @NotNull AbstractView createView(
		final int rows,
		final String title,
		final @NotNull ViewType type
	) {
		checkTypeSupport(type);
		return new View(rows, title, type);
	}

	@Override
	public @NotNull ViewContainer createContainer(
		final @NotNull VirtualView view,
		final int size,
		final String title,
		final @NotNull ViewType type
	) {
		checkTypeSupport(type);
		return new BukkitChestViewContainer(Bukkit.createInventory((View) view, size, title));
	}

	@Override
	public @NotNull Viewer createViewer(Object... parameters) {
		final Object playerObject = parameters[0];
		if (!(playerObject instanceof Player))
			throw new IllegalArgumentException("createViewer(...) first parameter must be a Player");

		return new BukkitViewer((Player) playerObject);
	}

	@Override
	public @NotNull BaseViewContext createContext(
		final @NotNull AbstractView view,
		final @NotNull ViewContainer container
	) {
		return new BukkitViewContext(
			view,
			container
		);
	}

	@Override
	public boolean worksInCurrentPlatform() {
		synchronized (this) {
			if (worksInCurrentPlatform != null)
				return worksInCurrentPlatform;

			try {
				Class.forName("org.bukkit.Bukkit");
				worksInCurrentPlatform = true;
			} catch (ClassNotFoundException ignored) {
				// suppress RuntimeException because it will be thrown in PlatformUtils
				worksInCurrentPlatform = false;
			}

			return worksInCurrentPlatform;
		}
	}

	private void checkTypeSupport(@NotNull ViewType type) {
		if (type == ViewType.CHEST)
			return;

		throw new IllegalArgumentException(String.format(
			"%s ViewType is not supported in Bukkit platform.",
			type.getIdentifier()
		));
	}

}