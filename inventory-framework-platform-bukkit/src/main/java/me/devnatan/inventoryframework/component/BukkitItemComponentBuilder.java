package me.devnatan.inventoryframework.component;

import java.util.function.Consumer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unchecked")
public class BukkitItemComponentBuilder<SELF extends BukkitItemComponentBuilder<SELF>>
	extends BukkitComponentBuilder<SELF>
	implements ItemComponentBuilder<SELF, ItemStack> {

    private int slot;
    private ItemStack item;

	public BukkitItemComponentBuilder() {
	}

	/**
	 * Defines the item that will be used as fallback for rendering in the slot where this item is
	 * positioned. The fallback item is always static.
	 *
	 * @param item The new fallback item stack.
	 * @return This item builder.
	 */
	@Override
	public final SELF withItem(@Nullable ItemStack item) {
		this.item = item;
		return (SELF) this;
	}

    @Override
    public final SELF withSlot(int slot) {
        this.slot = slot;
        return (SELF) this;
    }

    @Override
    public final SELF withSlot(int row, int column) {
        // FIXME Missing root availability, root must be available
        // final ViewContainer container = ((IFRenderContext) root).getContainer();
        // return withSlot(SlotConverter.convertSlot(row, column, container.getRowsCount(),
        // container.getColumnsCount()));
        return (SELF) this;
    }

	@Override
	public final boolean isContainedWithin(int position) {
		return position == slot;
	}

    @SuppressWarnings("unchecked")
	@Override
    public ItemComponent build(VirtualView root) {
        return new BukkitItemComponent(
                slot,
                item,
                getKey(),
                root,
                getReference(),
                getWatchingStates(),
                getDisplayCondition(),
                getRenderHandler(),
                getUpdateHandler(),
                (Consumer<? super IFSlotClickContext>) getClickHandler(),
                isCancelOnClick(),
                isCloseOnClick(),
                isUpdateOnClick());
    }

	@Override
	public String toString() {
		return "BukkitItemComponentBuilder{" + "slot=" + slot + ", item=" + item + "} " + super.toString();
	}
}
