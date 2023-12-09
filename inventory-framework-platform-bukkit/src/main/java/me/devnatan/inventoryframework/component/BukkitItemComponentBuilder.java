package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.bukkit.inventory.ItemStack;

public class BukkitItemComponentBuilder<SELF> extends BukkitComponentBuilder<SELF> implements ItemComponentBuilder {

    private int position;
    private int row = -1, column = -1;
    private ItemStack item;

    public BukkitItemComponentBuilder() {}

	@Override
	public Component buildComponent(VirtualView root) {
		final Component component = new BukkitItemComponentImpl(
			getPosition(),
			getItem(),
			getKey(),
			root,
			getReference(),
			getWatchingStates(),
			getDisplayCondition(),
			getRenderHandler(),
			getUpdateHandler(),
			getClickHandler(),
			isCancelOnClick(),
			isCloseOnClick(),
			isUpdateOnClick());
		component.setHandle(new BukkitItemComponentImplHandle());
		return component;
	}

	protected final int getPosition() {
        return position;
    }

    @Override
    public final void setPosition(int position) {
        this.position = position;
    }

    protected final int getRow() {
        return row;
    }

    protected final void setRow(int row) {
        this.row = row;
    }

    protected final int getColumn() {
        return column;
    }

    protected final void setColumn(int column) {
        this.column = column;
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

    @SuppressWarnings("unchecked")
    public final SELF withSlot(int slot) {
        setPosition(slot);
        return (SELF) this;
    }

    @SuppressWarnings("unchecked")
    public final SELF withSlot(int row, int column) {
        setRow(row);
        setColumn(column);
        return (SELF) this;
    }

    @SuppressWarnings("unchecked")
    public final SELF withItem(ItemStack item) {
        setItem(item);
        return (SELF) this;
    }

    @Override
    public String toString() {
        return "BukkitItemComponentBuilder{" + "item=" + item + "} " + super.toString();
    }
}
