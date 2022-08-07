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
        this.value = value instanceof ItemWrapper ? ((ItemWrapper) value).getValue() : value;

        checkValueType();
    }

    /**
     * Returns the current stored value as a Bukkit platform item.
     *
     * @return A Bukkit ItemStack.
     */
    private ItemStack asBukkitItem() {
        return value == null ? null : (ItemStack) value;
    }

    /**
     * Checks if this wrapper is empty.
     *
     * @return <code>true</code> if this has any valid item or <code>false</code> otherwise.
     */
    private boolean isEmpty() {
        return value == null;
    }

    /**
     * Checks if current wrapped type is supported.
     *
     * @throws IllegalStateException If value type is not supported.
     */
    private void checkValueType() throws IllegalStateException {
        if (value == null) return;

        if (!(value instanceof ItemStack))
            throw new IllegalStateException(String.format(
                    "Input not supported by item wrapper: %s", value.getClass().getName()));
    }
}
