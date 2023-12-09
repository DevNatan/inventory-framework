package me.devnatan.inventoryframework.component;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentContext;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.State;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * {@link ItemComponent} implementation for Bukkit platform.
 */
public final class BukkitItemComponentImpl extends PlatformComponent<Context, Void> implements ItemComponent {

    private int position;
    private final ItemStack stack;

    BukkitItemComponentImpl(
            int position,
            ItemStack itemStack,
            String key,
            VirtualView root,
            Ref<Component> reference,
            Set<State<?>> watchingStates,
            Predicate<? extends IFContext> displayCondition,
            Consumer<? super IFComponentRenderContext> renderHandler,
            Consumer<? super IFComponentUpdateContext> updateHandler,
            Consumer<? super IFSlotClickContext> clickHandler,
            boolean cancelOnClick,
            boolean closeOnClick,
            boolean updateOnClick) {
        super(
                key,
                root,
                reference,
                watchingStates,
                displayCondition,
                renderHandler,
                updateHandler,
                clickHandler,
                cancelOnClick,
                closeOnClick,
                updateOnClick);
        this.position = position;
        this.stack = itemStack;
        setHandle(new Handle(this));
    }

    @Override
    public int getPosition() {
        return position;
    }

    void setPosition(int position) {
        this.position = position;
    }

    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public Object getPlatformItem() {
        return getItemStack();
    }

    @Override
    public boolean intersects(@NotNull Component other) {
        if (other == this) return true;
        if (other instanceof ItemComponent) return getPosition() == ((ItemComponent) other).getPosition();

        return other.intersects(this);
    }

    @Override
    public boolean isContainedWithin(int position) {
        return getPosition() == position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitItemComponentImpl that = (BukkitItemComponentImpl) o;
        return getPosition() == that.getPosition() && Objects.equals(getItemStack(), that.getItemStack());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition(), getItemStack());
    }

    @Override
    public String toString() {
        return "BukkitItemComponentImpl{" + "position=" + position + ", itemStack=" + stack + "} " + super.toString();
    }
}

class Handle extends BukkitComponentHandle<BukkitItemComponentBuilder<Void>> {

    private BukkitItemComponentImpl component;

    Handle(BukkitItemComponentImpl component) {
        this.component = component;
    }

    @Override
    public void rendered(@NotNull ComponentRenderContext context) {
        final BukkitItemComponentImpl component = (BukkitItemComponentImpl) context.getComponent();

        if (component.getRenderHandler() != null) {
            final int initialSlot = component.getPosition();
            component.getRenderHandler().accept(context);

            // Externally managed components have its own displacement measures
            // FIXME Missing implementation
            // TODO Component-based context do not need displacement measures?
            if (!component.isManagedExternally()) {
                final int updatedSlot = ((BukkitItemComponentImpl) context.getComponent()).getPosition();
                component.setPosition(updatedSlot);

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

            // context.getContainer().renderItem(getPosition(), context.getResult());
            component.setVisible(true);
            return;
        }

        if (component.getItemStack() == null) {
            if (context.getContainer().getType().isResultSlot(component.getPosition())) {
                component.show();
                return;
            }
            throw new IllegalStateException("At least one fallback item or render handler must be provided");
        }

        context.getContainer().renderItem(component.getPosition(), component.getItemStack());
        component.show();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updated(@NotNull IFComponentUpdateContext context) {
        if (context.isCancelled()) return;

        @SuppressWarnings("rawtypes")
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
    public void cleared(@NotNull IFComponentContext context) {
        final Component component = context.getComponent();
        component.getContainer().removeItem(((ItemComponent) component).getPosition());
    }

    @Override
    public void clicked(@NotNull IFSlotClickContext context) {
        @SuppressWarnings("rawtypes")
        final PlatformComponent component = (PlatformComponent) context.getComponent();
        if (component.isUpdateOnClick()) context.update();
    }

    @Override
    public BukkitItemComponentBuilder<Void> builder() {
        return new BukkitItemComponentBuilder<Void>() {
            @Override
            public Component buildComponent(VirtualView root) {
                return new BukkitItemComponentImpl(
                        getPosition(),
                        getItem(),
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
                        isUpdateOnClick());
            }
        };
    }
}
