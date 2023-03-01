package me.devnatan.inventoryframework.component;

import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@Getter(AccessLevel.PACKAGE)
public final class BukkitItemBuilder
	extends DefaultComponentBuilder<BukkitItemBuilder>
	implements ItemBuilder<BukkitItemBuilder> {

	private int slot;
	private ItemStack item;

	/**
	 * Defines the item that will be used as fallback for rendering in the slot where this item is
	 * positioned. The fallback item is always static.
	 *
	 * @param item The new fallback item stack.
	 * @return This item builder.
	 */
	public BukkitItemBuilder withItem(@Nullable ItemStack item) {
		this.item = item;
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BukkitItemBuilder withSlot(int slot) {
		this.slot = slot;
		return this;
	}
}
