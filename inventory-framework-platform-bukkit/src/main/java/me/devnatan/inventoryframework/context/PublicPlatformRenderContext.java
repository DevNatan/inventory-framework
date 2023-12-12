package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import org.bukkit.inventory.ItemStack;

public class PublicPlatformRenderContext
        extends PublicComponentRenderContext<PublicPlatformRenderContext, BukkitItemComponentBuilder, ItemStack> {

    private ItemStack item;

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    protected BukkitItemComponentBuilder createItemBuilder() {
        return new BukkitItemComponentBuilder();
    }
}
