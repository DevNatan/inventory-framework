package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
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
		PlatformUtils.removeFactory();
		return new View(rows, title, type);
	}

	@Override
	public @NotNull ViewContainer createContainer(
		final @NotNull VirtualView view,
		final int size,
		final String title,
		final ViewType type
	) {
		final ViewType finalType;
		if (type == null)
			finalType = ViewType.CHEST;
		else checkTypeSupport((finalType = type));

		final Inventory inventory;
		if (title == null) {
			inventory = size == 0
				? Bukkit.createInventory((InventoryHolder) view, toInventoryType(finalType))
				: Bukkit.createInventory((InventoryHolder) view, size);
		} else if (size == 0)
			inventory = Bukkit.createInventory((InventoryHolder) view, toInventoryType(finalType), title);
		else
			inventory = Bukkit.createInventory((InventoryHolder) view, size, title);

		return new BukkitChestViewContainer(inventory);
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
		final ViewContainer container,
		final Class<? extends ViewContext> backingContext
	) {
		if (backingContext != null && OpenViewContext.class.isAssignableFrom(backingContext))
			return new BukkitOpenViewContext(view);

		return view instanceof PaginatedView
			? new BukkitPaginatedViewContext<>(view, container)
			: new BukkitViewContext(view, container);
	}

	@Override
	public synchronized boolean worksInCurrentPlatform() {
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

	private InventoryType toInventoryType(@NotNull ViewType type) {
		return InventoryType.CHEST;
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