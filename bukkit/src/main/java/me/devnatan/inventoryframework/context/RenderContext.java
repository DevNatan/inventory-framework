package me.devnatan.inventoryframework.context;

import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import me.devnatan.inventoryframework.component.BukkitItemBuilder;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.state.StateHost;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.devnatan.inventoryframework.utils.SlotConverter.convertSlot;

@Getter
public final class RenderContext extends ConfinedContext implements IFRenderContext, Context {

	private final @NotNull IFContext parent;
	private final @NotNull Player player;

	@Getter(AccessLevel.PRIVATE)
	private final ViewConfigBuilder inheritedConfigBuilder = new ViewConfigBuilder();

	@Getter(AccessLevel.PROTECTED)
	private final List<ComponentBuilder<?>> componentBuilders = new ArrayList<>();

	@ApiStatus.Internal
	public RenderContext(
		@NotNull RootView root,
		@NotNull ViewContainer container,
		@NotNull Viewer viewer,
		@NotNull IFContext parent) {
		super(root, container, viewer);
		this.player = ((BukkitViewer) viewer).getPlayer();
		this.parent = parent;
	}

	@Override
	public @NotNull UUID getId() {
		return getParent().getId();
	}

	@Override
	public @NotNull StateHost getStateHost() {
		return getParent().getStateHost();
	}

	@Override
	public @NotNull ViewConfigBuilder modifyConfig() {
		return inheritedConfigBuilder;
	}

	/**
	 * Adds an item to a specific slot in the context container.
	 *
	 * @param slot The slot in which the item will be positioned.
	 * @return An item builder to configure the item.
	 */
	public @NotNull BukkitItemBuilder slot(int slot) {
		return createItemBuilder().withSlot(slot);
	}

	/**
	 * Adds an item to a specific slot in the context container.
	 *
	 * @param slot The slot in which the item will be positioned.
	 * @return An item builder to configure the item.
	 */
	public @NotNull BukkitItemBuilder slot(int slot, @Nullable ItemStack item) {
		return createItemBuilder().withSlot(slot).withItem(item);
	}

	/**
	 * Adds an item at the specific column and ROW (X, Y) in that context's container.
	 *
	 * @param row    The row (Y) in which the item will be positioned.
	 * @param column The column (X) in which the item will be positioned.
	 * @return An item builder to configure the item.
	 */
	@NotNull
	public BukkitItemBuilder slot(int row, int column) {
		return createItemBuilder().withSlot(
			convertSlot(row, column, getContainer().getRowsCount(), getContainer().getColumnsCount())
		);
	}

	/**
	 * Adds an item at the specific column and ROW (X, Y) in that context's container.
	 *
	 * @param row    The row (Y) in which the item will be positioned.
	 * @param column The column (X) in which the item will be positioned.
	 * @return An item builder to configure the item.
	 */
	@NotNull
	public BukkitItemBuilder slot(int row, int column, @Nullable ItemStack item) {
		return createItemBuilder().withSlot(
			convertSlot(row, column, getContainer().getRowsCount(), getContainer().getColumnsCount())
		).withItem(item);
	}

	/**
	 * Adds an item to the first slot of this context's container.
	 *
	 * @return An item builder to configure the item.
	 */
	public @NotNull BukkitItemBuilder firstSlot() {
		return createItemBuilder().withSlot(getContainer().getFirstSlot());
	}

	/**
	 * Adds an item to the first slot of this context's container.
	 *
	 * @return An item builder to configure the item.
	 */
	public @NotNull BukkitItemBuilder lastSlot() {
		return createItemBuilder().withSlot(getContainer().getLastSlot());
	}

	// TODO documentation
	public @NotNull BukkitItemBuilder availableSlot() {
		throw new UnsupportedOperationException();
	}

	// TODO documentation
	public @NotNull BukkitItemBuilder layoutSlot(String character) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Creates a BukkitItemBuilder instance and registers it in this context.
	 *
	 * @return A new registered BukkitItemBuilder instance.
	 */
	private BukkitItemBuilder createItemBuilder() {
		final BukkitItemBuilder builder = new BukkitItemBuilder();
		componentBuilders.add(builder);
		return builder;
	}

}
