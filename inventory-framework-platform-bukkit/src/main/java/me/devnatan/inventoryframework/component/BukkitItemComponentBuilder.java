package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.Context;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unchecked")
public class BukkitItemComponentBuilder<SELF>
	extends PlatformItemComponentBuilder<SELF, Context>
	implements ItemComponentBuilder {

    private ItemStack item;

	public BukkitItemComponentBuilder() {
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		this.item = item;
	}

	@Override
    public ItemComponent build(VirtualView root) {
        return new BukkitItemComponent(
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
    }

	@Override
	public SELF withSlot(int row, int column) {
		return null;
	}

	@Override
	public String toString() {
		return "BukkitItemComponentBuilder{" + "item=" + item + "} " + super.toString();
	}

}
