package me.devnatan.inventoryframework.component;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;

// TODO Make this render abstract and remove `getResult` (Object) from IFSlotRenderContext
final class BukkitItemComponentImpl extends PlatformComponent<Context, BukkitItemComponentBuilder> {

    private int position;
    private final Object stack;

    BukkitItemComponentImpl(
            int position,
            Object stack,
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
        this.stack = stack;
    }

    public int getPosition() {
        return position;
    }

    public Object getStack() {
        return stack;
    }

    @Override
    public boolean intersects(@NotNull Component other) {
        if (!(other instanceof ItemComponent)) return other.intersects(this);

        return getPosition() == ((ItemComponent) other).getPosition();
    }

    @Override
    public boolean isContainedWithin(int position) {
        return getPosition() == position;
    }

    @Override
    public void render(@NotNull IFComponentRenderContext context) {
        if (getRenderHandler() != null) {
            final int initialSlot = getPosition();
            getRenderHandler().accept(context);

            // Externally managed components have its own displacement measures
            // FIXME Missing implementation
            // TODO Component-based context do not need displacement measures?
            if (!isManagedExternally()) {
                final int updatedSlot = ((BukkitItemComponentImpl) context.getComponent()).getPosition();
                position = updatedSlot;

                if (updatedSlot == -1 && initialSlot == -1) {
                    // TODO needs more user-friendly "do something"-like message
                    throw new InventoryFrameworkException("Missing position (unset slot) for item component");
                }

                // TODO Misplaced - move this to overall item component misplacement check
                if (initialSlot != -1 && initialSlot != updatedSlot) {
                    context.getContainer().removeItem(initialSlot);
                    setVisible(false);
                }
            }

            // context.getContainer().renderItem(getPosition(), context.getResult());
            setVisible(true);
            return;
        }

        if (getStack() == null) {
            if (context.getContainer().getType().isResultSlot(getPosition())) {
                setVisible(true);
                return;
            }
            throw new IllegalStateException("At least one fallback item or render handler must be provided");
        }

        context.getContainer().renderItem(getPosition(), getStack());
        setVisible(true);
    }

    @Override
    public void updated(@NotNull IFComponentUpdateContext context) {
        if (context.isCancelled()) return;

        // Static item with no `displayIf` must not even reach the update handler
        if (!context.isForceUpdate() && getDisplayCondition() == null && getRenderHandler() == null) return;

        if (isVisible() && getUpdateHandler() != null) {
            getUpdateHandler().accept(context);
            if (context.isCancelled()) return;
        }

        context.getRoot().renderComponent(this);
    }

    @Override
    public void cleared(@NotNull IFRenderContext context) {
        context.getContainer().removeItem(getPosition());
    }

    @Override
    public void clicked(@NotNull IFSlotClickContext context) {
        if (getClickHandler() != null) getClickHandler().accept(context);
        if (isUpdateOnClick()) context.update();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BukkitItemComponentImpl that = (BukkitItemComponentImpl) o;
        return getPosition() == that.getPosition() && Objects.equals(getStack(), that.getStack());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition(), getStack());
    }

    @Override
    public String toString() {
        return "BukkitItemComponentImpl{" + "position=" + position + ", stack=" + stack + "} " + super.toString();
    }
}
