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

    private ComponentRenderContext delegate() {
        return (ComponentRenderContext) getConfinedContext();
    }

    public ItemStack getItem() {
        return delegate().getItem();
    }

    public void setItem(ItemStack item) {
        delegate().setItem(item);
    }

    public int getSlot() {
        return delegate().getSlot();
    }

    public void setSlot(int slot) {
        delegate().setSlot(slot);
    }

    public void setSlot(int row, int column) {
        delegate().setSlot(row, column);
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
