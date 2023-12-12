package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.bukkit.inventory.ItemStack;

public final class BukkitItemComponentBuilder extends BukkitComponentBuilder<BukkitItemComponentBuilder>
        implements ItemComponentBuilder {

    private int position;
    private int row = -1, column = -1;
    private ItemStack item;

    public BukkitItemComponentBuilder() {}

    @Override
    public ComponentHandle buildHandle() {
        return new BukkitItemComponentImplHandle();
    }

    @Override
    public Component buildComponent(VirtualView root) {
        return new BukkitItemComponentImpl(
                position,
                item,
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
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public ItemComponentBuilder withPosition(int position) {
        setPosition(position);
        return this;
    }

    @Override
    public boolean isContainedWithin(int position) {
        return this.position == position;
    }

    public BukkitItemComponentBuilder withSlot(int slot) {
        setPosition(slot);
        return this;
    }

    public BukkitItemComponentBuilder withSlot(int row, int column) {
        this.row = row;
        this.column = column;
        return this;
    }

    public BukkitItemComponentBuilder withItem(ItemStack item) {
        this.item = item;
        return this;
    }

    @Override
    public ItemComponentBuilder withPlatformItem(Object item) {
        return withItem((ItemStack) item);
    }

    @Override
    public String toString() {
        return "BukkitItemComponentBuilder{" + "item=" + item + ", position=" + position + "} " + super.toString();
    }
}
