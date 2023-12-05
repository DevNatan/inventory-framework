package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public class BukkitInternalSlotComponentBuilder extends BukkitComponentBuilder<BukkitInternalSlotComponentBuilder>
        implements ItemComponentBuilder {

    private int position;
    private ItemStack item;

    public BukkitInternalSlotComponentBuilder() {}

    @Override
    public final void setPosition(int position) {
        this.position = position;
    }

    public final BukkitInternalSlotComponentBuilder withSlot(int slot) {
        setPosition(slot);
        return this;
    }

    public final BukkitInternalSlotComponentBuilder withSlot(int row, int column) {
        // FIXME Missing root availability, root must be available
        // final ViewContainer container = ((IFRenderContext) root).getContainer();
        // return withSlot(SlotConverter.convertSlot(row, column, container.getRowsCount(),
        // container.getColumnsCount()));
        // return (SELF) this;
        return this;
    }

    public final BukkitInternalSlotComponentBuilder withItem(ItemStack item) {
        this.item = item;
        return this;
    }

    @Override
    public final boolean isContainedWithin(int position) {
        return this.position == position;
    }

    @Override
    public ItemComponent build(VirtualView root) {
        return new BukkitItemComponent(
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
    public String toString() {
        return "BukkitItemComponentBuilder{" + "item=" + item + "} " + super.toString();
    }
}
