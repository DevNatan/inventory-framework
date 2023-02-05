package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.internal.BukkitViewContainer;
import me.devnatan.inventoryframework.internal.BukkitViewer;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class SlotContext extends ConfinedContext implements ViewContext, IFSlotContext {

	private final int slot;
	private final Player player;
	private final IFContext parent;

	public SlotContext(
		@NotNull RootView root,
		@NotNull ViewContainer container,
		@NotNull Viewer viewer,
		int slot,
		@NotNull IFContext parent
	) {
		super(root, container, viewer);
		this.slot = slot;
		this.player = ((BukkitViewer) viewer).getPlayer();
		this.parent = parent;
	}

	@Override
	public final int getSlot() {
		return slot;
	}

	@Override
	public final IFContext getParent() {
		return parent;
	}

	@Override
	public @NotNull Player getPlayer() {
		return player;
	}

	public ItemStack getItem() {
		return ((BukkitViewContainer) getContainer()).getInventory().getItem(getSlot());
	}
}
