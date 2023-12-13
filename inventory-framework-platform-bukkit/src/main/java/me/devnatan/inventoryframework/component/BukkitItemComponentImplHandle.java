package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.PublicPlatformRenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class BukkitItemComponentImplHandle extends BukkitComponentHandle<BukkitItemComponentBuilder> {

    @Override
    public void rendered(PublicPlatformRenderContext context) {
        final PlatformComponent component = (PlatformComponent) context.getComponent();

        if (component.getRenderHandler() != null) {
            final int initialSlot = component.getPosition();
            component.getRenderHandler().accept(context.getConfinedContext());

            // Externally managed components have its own displacement measures
            // FIXME Missing implementation
            // TODO Component-based context do not need displacement measures?
            if (!component.isManagedExternally()) {
                final int updatedSlot = component.getPosition();
                //                context.setPosition(updatedSlot);

                if (updatedSlot == -1 && initialSlot == -1) {
                    // TODO needs more user-friendly "do something"-like message
                    throw new InventoryFrameworkException("Missing position (unset slot) for item component");
                }

                // TODO Misplaced - move this to overall item component misplacement check
                if (initialSlot != -1 && initialSlot != updatedSlot) {
                    context.getContainer().removeItem(initialSlot);
                    component.hide();
                }
            }

            context.getContainer().renderItem(component.getPosition(), context.getItem());
            component.setVisible(true);
            return;
        }

        if (context.getItem() == null) {
            if (context.getContainer().getType().isResultSlot(component.getPosition())) {
                component.setVisible(true);
                return;
            }
            if (!component.isManagedExternally())
                throw new IllegalStateException("At least one fallback item or render handler must be provided");
            return;
        }

        context.getContainer().renderItem(component.getPosition(), context.getItem());
        component.setVisible(true);
    }

    @Override
    protected void updated(ComponentUpdateContext context) {
        if (context.isCancelled()) return;

        final PlatformComponent component = (PlatformComponent) context.getComponent();

        // Static item with no `displayIf` must not even reach the update handler
        if (!context.isForceUpdate() && component.getDisplayCondition() == null && component.getRenderHandler() == null)
            return;

        if (component.isVisible() && component.getUpdateHandler() != null) {
            component.getUpdateHandler().accept(context);
            if (context.isCancelled()) return;
        }

        ((IFRenderContext) context.getTopLevelContext()).renderComponent(component);
    }

    @Override
    protected void cleared(ComponentClearContext context) {
        if (context.isCancelled()) return;
        final Component component = context.getComponent();
        component.getContainer().removeItem(component.getPosition());
    }

    @Override
    protected void clicked(SlotClickContext context) {
        final PlatformComponent component = (PlatformComponent) context.getComponent();
        if (component.getClickHandler() != null) component.getClickHandler().accept(context);

        if (context.isCancelled()) return;
        if (component.isUpdateOnClick()) component.update();
        if (component.isCloseOnClick()) context.closeForPlayer();
    }

    @Override
    public BukkitItemComponentBuilder builder() {
        return new BukkitItemComponentBuilder();
    }
}
