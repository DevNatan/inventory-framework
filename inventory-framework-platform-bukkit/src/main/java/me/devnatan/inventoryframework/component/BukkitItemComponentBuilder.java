package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.bukkit.inventory.ItemStack;

public class BukkitItemComponentBuilder<SELF>
	extends BukkitComponentBuilder<SELF>
	implements ItemComponentBuilder {

	private int position;
    private ItemStack item;

	public BukkitItemComponentBuilder() {}

	protected final int getPosition() {
		return position;
	}

	@Override
	public final void setPosition(int position) {
		this.position = position;
	}

	@Override
	public final boolean isContainedWithin(int position) {
		return this.position == position;
	}

	protected final ItemStack getItem() {
        return item;
    }

    protected final void setItem(ItemStack item) {
        this.item = item;
    }

	public final SELF withSlot(int slot) {
		setPosition(slot);
		return (SELF) this;
	}

	public final SELF withSlot(int row, int column) {
		// FIXME Missing root availability, root must be available
		// final ViewContainer container = ((IFRenderContext) root).getContainer();
		// return withSlot(SlotConverter.convertSlot(row, column, container.getRowsCount(),
		// container.getColumnsCount()));
		// return (SELF) this;
		return (SELF) this;
	}

	public final SELF withItem(ItemStack item) {
		this.item = item;
		return (SELF) this;
	}

    @Override
    public String toString() {
        return "BukkitItemComponentBuilder{" + "item=" + item + "} " + super.toString();
    }
}
