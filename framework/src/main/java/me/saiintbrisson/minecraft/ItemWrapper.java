package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Item wrapper that wraps platform-specific item stack types.
 */
@ToString
public final class ItemWrapper {

	@Nullable
	@Getter(AccessLevel.PACKAGE)
	private final Object value;

	ItemWrapper(@Nullable Object value) {
		if (value instanceof ItemWrapper)
			throw new IllegalStateException("Value cannot be a ItemWrapper");
		this.value = value;
	}

	/**
	 * Returns the current stored value as a Bukkit platform item.
	 *
	 * @return A Bukkit ItemStack.
	 */
	private ItemStack asBukkitItem() {
		return (ItemStack) value;
	}

}
