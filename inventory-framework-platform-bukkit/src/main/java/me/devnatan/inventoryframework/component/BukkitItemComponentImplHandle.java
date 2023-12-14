package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.PublicComponentRenderContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class BukkitItemComponentImplHandle extends BukkitComponentHandle<BukkitItemComponentBuilder> {

    @Override
    public void rendered(PublicComponentRenderContext context) {
        //        final PlatformComponent component = (PlatformComponent) context.getComponent();
        //
        //        if (component.getRenderHandler() != null) {
        //            final int initialSlot = component.getPosition();
        //            component.getRenderHandler().accept(context.getConfinedContext());
        //
        //            // Externally managed components have its own displacement measures
        //            // FIXME Missing implementation
        //            // TODO Component-based context do not need displacement measures?
        //            if (!component.isManagedExternally()) {
        //                final int updatedSlot = component.getPosition();
        //                //                context.setPosition(updatedSlot);
        //
        //                if (updatedSlot == -1 && initialSlot == -1) {
        //                    // TODO needs more user-friendly "do something"-like message
        //                    throw new InventoryFrameworkException("Missing position (unset slot) for item component");
        //                }
        //
        //                // TODO Misplaced - move this to overall item component misplacement check
        //                if (initialSlot != -1 && initialSlot != updatedSlot) {
        //                    context.getContainer().removeItem(initialSlot);
        //                    component.hide();
        //                }
        //            }
        //
        //            context.getContainer().renderItem(component.getPosition(), context.getItem());
        //            component.setVisible(true);
        //            return;
        //        }
        //
        //        if (context.getItem() == null) {
        //            if (context.getContainer().getType().isResultSlot(component.getPosition())) {
        //                component.setVisible(true);
        //                return;
        //            }
        //            if (!component.isManagedExternally())
        //                throw new IllegalStateException("At least one fallback item or render handler must be
        // provided");
        //            return;
        //        }
        //
        //        context.getContainer().renderItem(component.getPosition(), context.getItem());
        //        component.setVisible(true);
    }

    @Override
    public BukkitItemComponentBuilder builder() {
        return new BukkitItemComponentBuilder();
    }
}
