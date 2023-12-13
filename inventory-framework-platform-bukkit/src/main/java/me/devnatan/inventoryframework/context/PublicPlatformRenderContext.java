package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public final class PublicPlatformRenderContext
        extends PublicComponentRenderContext<PublicPlatformRenderContext, BukkitItemComponentBuilder, ItemStack> {

    private ItemStack item;

    @ApiStatus.Internal
    public PublicPlatformRenderContext(IFComponentRenderContext componentContext) {
        super(componentContext);
        this.item = ((ComponentRenderContext) componentContext).getItem();
    }

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

    @Override
    public String toString() {
        return "PublicPlatformRenderContext{" + "item=" + item + "} " + super.toString();
    }
}
