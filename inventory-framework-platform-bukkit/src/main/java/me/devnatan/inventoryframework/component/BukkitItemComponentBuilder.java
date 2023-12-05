package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.bukkit.inventory.ItemStack;

public abstract class BukkitItemComponentBuilder<SELF> extends BukkitComponentBuilder<SELF>
        implements ItemComponentBuilder {

    private ItemStack item;

    protected BukkitItemComponentBuilder() {}

    protected final ItemStack getItem() {
        return item;
    }

    protected final void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public abstract ItemComponent build(VirtualView root);

    @Override
    public String toString() {
        return "BukkitItemComponentBuilder{" + "item=" + item + "} " + super.toString();
    }
}
