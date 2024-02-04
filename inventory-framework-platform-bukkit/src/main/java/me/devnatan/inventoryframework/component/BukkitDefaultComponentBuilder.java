package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.bukkit.inventory.ItemStack;

public class BukkitDefaultComponentBuilder extends BukkitComponentBuilder<BukkitDefaultComponentBuilder> {

    private ItemStack item;

    public BukkitDefaultComponentBuilder() {}

    @Override
    public Component buildComponent(VirtualView root) {
        return new BukkitItemComponentImpl(
                getPosition(),
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
                isUpdateOnClick(),
                isSelfManaged());
    }

    @Override
    public void setPosition(int position) {
        withSlot(position);
    }

    @Override
    public ItemComponentBuilder withPosition(int position) {
        return withSlot(position);
    }

    @Override
    public boolean isContainedWithin(int position) {
        return super.getPosition() == position;
    }

    public BukkitDefaultComponentBuilder withItem(ItemStack item) {
        this.item = item;
        return this;
    }

    @Override
    public ItemComponentBuilder withPlatformItem(Object item) {
        return withItem((ItemStack) item);
    }

    @Override
    public String toString() {
        return "BukkitDefaultComponentBuilder{" + "item=" + item + ", position=" + getPosition() + "} " + super.toString();
    }
}
