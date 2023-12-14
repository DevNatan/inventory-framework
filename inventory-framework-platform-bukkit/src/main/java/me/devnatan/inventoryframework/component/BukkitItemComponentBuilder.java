package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.bukkit.inventory.ItemStack;

public final class BukkitItemComponentBuilder extends BukkitComponentBuilder<BukkitItemComponentBuilder>
        implements ItemComponentBuilder {

    private ItemStack item;

    public BukkitItemComponentBuilder() {}

    @Override
    public ComponentHandle buildHandle() {
        return new BukkitItemComponentImplHandle();
    }

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
        super.withSlot(position);
    }

    @Override
    public ItemComponentBuilder withPosition(int position) {
        setPosition(position);
        return this;
    }

    @Override
    public boolean isContainedWithin(int position) {
        return super.getPosition() == position;
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
        return "BukkitItemComponentBuilder{" + "item=" + item + ", position=" + getPosition() + "} " + super.toString();
    }
}
