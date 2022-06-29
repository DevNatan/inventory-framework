package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
public abstract class BukkitViewContainer implements ViewContainer {

    @NotNull
    abstract Inventory getInventory();

    @Override
    public void renderItem(int slot, Object item) {
        requireSupportedItem(item);
        getInventory().setItem(slot, convertItem(item));
    }

    @Override
    public void removeItem(int slot) {
        getInventory().setItem(slot, null);
    }

    @Override
    public boolean matchesItem(int slot, Object item, boolean exactly) {
        requireSupportedItem(item);
        final ItemStack target = getInventory().getItem(slot);
        if (target == null) return item == null;
        if (item instanceof ItemStack)
            return exactly ? target.equals(item) : target.isSimilar((ItemStack) item);
        if (item instanceof Material) return target.getType() == item;

        return false;
    }

    @Override
    public ItemStack convertItem(Object source) {
        requireSupportedItem(source);

        if (source instanceof ItemStack) return ((ItemStack) source).clone();
        if (source instanceof Material) return new ItemStack((Material) source);

        return null;
    }

    @Override
    public boolean isSupportedItem(Object item) {
        return item instanceof ItemStack || item instanceof Material;
    }

    protected final void requireSupportedItem(Object item) {
        if (isSupportedItem(item)) return;

        throw new IllegalStateException("Unsupported item type: " + item.getClass().getName());
    }

    @Override
    public boolean hasItem(int slot) {
        return getInventory().getItem(slot) != null;
    }

	@Override
	public @Nullable ItemWrapper getItem(int slot) {
		if (!hasItem(slot)) return null;
		return new ItemWrapper(getInventory().getItem(slot));
	}

	@Override
    public int getSize() {
        return getInventory().getSize();
    }

    @Override
    public int getSlotsCount() {
        return getInventory().getSize() - 1;
    }

    @Override
    public int getFirstSlot() {
        return 0;
    }

    @Override
    public int getLastSlot() {
        return getSlotsCount();
    }

    @Override
    public void changeTitle(@Nullable final String title) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEntityContainer() {
        return false;
    }

    @Override
    public final void open(@NotNull final Viewer viewer) {
        viewer.open(this);
    }

    @Override
    public final void close() {
        getInventory().getViewers().forEach(HumanEntity::closeInventory);
    }
}
