package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;

/**
 * Item wrapper that wraps platform-specific item stack types.
 */
@ToString
public final class ItemWrapper {

	@Getter(AccessLevel.PACKAGE)
	private final Object value;

	ItemWrapper(Object value) {
		if (value instanceof ItemWrapper)
			throw new IllegalStateException("Item wrapper value cannot be a ItemWrapper");
		this.value = value;
	}

	private ItemStack asBukkitItem() {
		return (ItemStack) value;
	}

}
