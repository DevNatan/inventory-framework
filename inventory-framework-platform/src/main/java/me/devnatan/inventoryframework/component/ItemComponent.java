package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.Ref;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

// TODO Make this render abstract and remove `getResult` (Object) from IFSlotRenderContext
public class ItemComponent extends AbstractComponent implements Component {

    private int position;
    private final Object stack;
    private final boolean cancelOnClick;
    private final boolean closeOnClick;
    private final Predicate<? extends IFContext> displayCondition;
    private final Consumer<? super IFComponentRenderContext> renderHandler;
    private final Consumer<? super IFComponentUpdateContext> updateHandler;
    private final Consumer<? super IFSlotClickContext> clickHandler;
    private final boolean updateOnClick;

    public ItemComponent(
            int position,
            Object stack,
            boolean cancelOnClick,
            boolean closeOnClick,
            Predicate<? extends IFContext> displayCondition,
            Consumer<? super IFComponentRenderContext> renderHandler,
            Consumer<? super IFComponentUpdateContext> updateHandler,
            Consumer<? super IFSlotClickContext> clickHandler,
            boolean updateOnClick) {
        this.position = position;
        this.stack = stack;
        this.cancelOnClick = cancelOnClick;
        this.closeOnClick = closeOnClick;
        this.displayCondition = displayCondition;
        this.renderHandler = renderHandler;
        this.updateHandler = updateHandler;
        this.clickHandler = clickHandler;
        this.updateOnClick = updateOnClick;
    }

    public int getPosition() {
        return position;
    }

    public Object getStack() {
        return stack;
    }

    public boolean isCancelOnClick() {
        return cancelOnClick;
    }

    public boolean isCloseOnClick() {
        return closeOnClick;
    }

    public boolean isUpdateOnClick() {
        return updateOnClick;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean shouldRender(IFContext context) {
        return displayCondition == null || ((Predicate<? super IFContext>) displayCondition).test(context);
    }

    public Consumer<? super IFComponentRenderContext> getRenderHandler() {
        return renderHandler;
    }

    public Consumer<? super IFComponentUpdateContext> getUpdateHandler() {
        return updateHandler;
    }

    public Consumer<? super IFSlotClickContext> getClickHandler() {
        return clickHandler;
    }

    @Override
    public boolean isContainedWithin(int position) {
        return getPosition() == position;
    }

    @Override
    public void rendered(@NotNull IFComponentRenderContext context) {
        if (getRenderHandler() != null) {
            final int initialSlot = getPosition();
            getRenderHandler().accept(context);

            // Externally managed components have its own displacement measures
			// TODO Component-based context do not need displacement measures?
//            if (!isManagedExternally()) {
//                final int updatedSlot = context.getComponent().getPosition();
//                position = updatedSlot;
//
//                if (updatedSlot == -1 && initialSlot == -1) {
//                    // TODO needs more user-friendly "do something"-like message
//                    throw new InventoryFrameworkException("Missing position (unset slot) for item component");
//                }
//
//                // TODO Misplaced - move this to overall item component misplacement check
//                if (initialSlot != -1 && initialSlot != updatedSlot) {
//                    context.getContainer().removeItem(initialSlot);
//                    setVisible(false);
//                }
//            }

            context.getContainer().renderItem(getPosition(), context.getResult());
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
        if (!context.isForceUpdate() && displayCondition == null && getRenderHandler() == null) return;

        if (isVisible() && getUpdateHandler() != null) {
            getUpdateHandler().accept(context);
            if (context.isCancelled()) return;
        }

        context.getRoot().renderComponent(this);
    }

    @Override
    public void cleared(@NotNull IFContext context) {
        ((IFRenderContext) context).getContainer().removeItem(getPosition());
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
        ItemComponent that = (ItemComponent) o;
        return getPosition() == that.getPosition() && Objects.equals(getStack(), that.getStack());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPosition(), getStack());
    }

    @Override
    public String toString() {
        return "ItemComponent{" + ", position="
                + position + ", stack="
                + stack + ", cancelOnClick="
                + cancelOnClick + ", closeOnClick="
                + closeOnClick + ", displayCondition="
                + displayCondition + ", renderHandler="
                + renderHandler + ", updateHandler="
                + updateHandler + ", clickHandler="
                + clickHandler + "}";
    }
}
