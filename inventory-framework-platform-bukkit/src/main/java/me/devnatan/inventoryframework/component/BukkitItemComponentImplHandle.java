package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.PublicComponentRenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class BukkitItemComponentImplHandle extends BukkitComponentHandle<BukkitItemComponentBuilder> {

    @Override
    protected void rendered(PublicComponentRenderContext context) {
        super.rendered(context);

        final PlatformComponent component = (PlatformComponent) context.getComponent();
        final int position = context.getSlot();
        final ItemStack item = context.getItem();
        if (item == null) {
            if (context.getContainer().getType().isResultSlot(position)) {
                component.setVisible(true);
                return;
            }

            // TODO This error must be in slot creation and not on render
            //      so the developer will know where the error is
            throw new IllegalStateException("At least one fallback item or render handler must be provided for "
                    + component.getClass().getName());
        }

        context.getContainer().renderItem(position, item);
    }

    @Override
    public BukkitItemComponentBuilder builder() {
        return new BukkitItemComponentBuilder();
    }
}
