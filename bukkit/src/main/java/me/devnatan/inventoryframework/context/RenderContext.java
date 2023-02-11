package me.devnatan.inventoryframework.context;

import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitItem;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@Getter
public final class RenderContext extends ConfinedContext implements IFRenderContext<BukkitItem>, Context {

	@NotNull
	private final Player player;

	@Getter(AccessLevel.PRIVATE)
	private final ViewConfigBuilder inheritedConfigBuilder = new ViewConfigBuilder();

	@ApiStatus.Internal
	public RenderContext(@NotNull RootView root, @NotNull ViewContainer container, @NotNull Viewer viewer) {
		super(root, container, viewer);
		this.player = ((BukkitViewer) viewer).getPlayer();
	}

	@Override
	public @NotNull ViewConfigBuilder modifyConfig() {
		return inheritedConfigBuilder;
	}

	@Override
	public @NotNull BukkitItem layoutSlot(String character) {
		throw new UnsupportedOperationException();
	}

	public @NotNull BukkitItem layoutSlot(String character, ItemStack item) {
		return layoutSlot(character).item(item);
	}

	public @NotNull BukkitItem layoutSlot(String character, Supplier<ItemStack> itemRenderHandler) {
		return layoutSlot(character).rendered(itemRenderHandler);
	}

	@Override
	public @NotNull BukkitItem slot(int slot) {
		BukkitItem item = new BukkitItem(slot);
		addComponent(item);
		return item;
	}

	public @NotNull BukkitItem slot(int slot, ItemStack item) {
		return slot(slot).item(item);
	}

	public @NotNull BukkitItem slot(int slot, Supplier<ItemStack> itemRenderHandler) {
		return slot(slot).rendered(itemRenderHandler);
	}

	@Override
	public @NotNull BukkitItem slot(int row, int column) {
		throw new UnsupportedOperationException();
	}

	public @NotNull BukkitItem slot(int row, int column, ItemStack item) {
		return slot(row, column).item(item);
	}

	public @NotNull BukkitItem slot(int row, int column, Supplier<ItemStack> itemRenderHandler) {
		return slot(row, column).rendered(itemRenderHandler);
	}

	@Override
	public @NotNull BukkitItem firstSlot() {
		throw new UnsupportedOperationException();
	}

	public @NotNull BukkitItem firstSlot(ItemStack item) {
		return firstSlot().item(item);
	}

	public @NotNull BukkitItem firstSlot(Supplier<ItemStack> itemRenderHandler) {
		return lastSlot().rendered(itemRenderHandler);
	}

	@Override
	public @NotNull BukkitItem lastSlot() {
		throw new UnsupportedOperationException();
	}

	public @NotNull BukkitItem lastSlot(ItemStack item) {
		return lastSlot().item(item);
	}

	public @NotNull BukkitItem lastSlot(Supplier<ItemStack> itemRenderHandler) {
		return lastSlot().rendered(itemRenderHandler);
	}

	@Override
	public @NotNull BukkitItem availableSlot() {
		throw new UnsupportedOperationException();
	}

	public @NotNull BukkitItem availableSlot(ItemStack item) {
		return availableSlot().item(item);
	}

	public @NotNull BukkitItem availableSlot(Supplier<ItemStack> itemRenderHandler) {
		return availableSlot().rendered(itemRenderHandler);
	}
}
