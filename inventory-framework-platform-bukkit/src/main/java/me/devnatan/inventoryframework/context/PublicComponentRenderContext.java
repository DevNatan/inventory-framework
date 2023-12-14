package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

public final class PublicComponentRenderContext
        extends PublicPlatformComponentRenderContext<
                PublicComponentRenderContext, BukkitItemComponentBuilder, ItemStack> {

    @ApiStatus.Internal
    public PublicComponentRenderContext(IFComponentRenderContext componentContext) {
        super(componentContext);
    }

    public ItemStack getItem() {
        return ((ComponentRenderContext) getConfinedContext()).getItem();
    }

    public void setItem(ItemStack item) {
        ((ComponentRenderContext) getConfinedContext()).setItem(item);
    }

    @Override
    protected BukkitItemComponentBuilder createItemBuilder() {
        return new BukkitItemComponentBuilder().withSelfManaged(true);
    }

    @Override
    public String toString() {
        return "PublicPlatformRenderContext{} " + super.toString();
    }
}
