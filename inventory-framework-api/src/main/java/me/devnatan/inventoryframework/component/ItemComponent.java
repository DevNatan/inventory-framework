package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

// TODO Make this render abstract and remove `getResult` (Object) from IFSlotRenderContext
public class ItemComponent implements Component, InteractionHandler {

    private final String key = UUID.randomUUID().toString();
    private final VirtualView root;
    private int position;
    private final Object stack;
    private final boolean cancelOnClick;
    private final boolean closeOnClick;
    private final Predicate<? extends IFContext> displayCondition;
    private final Consumer<? super IFSlotRenderContext> renderHandler;
    private final Consumer<? super IFSlotContext> updateHandler;
    private final Consumer<? super IFSlotClickContext> clickHandler;
    private final Set<State<?>> watching;
    private final boolean isManagedExternally;
    private final boolean updateOnClick;
    private boolean isVisible;

    public ItemComponent(
            VirtualView root,
            int position,
            Object stack,
            boolean cancelOnClick,
            boolean closeOnClick,
            Predicate<? extends IFContext> displayCondition,
            Consumer<? super IFSlotRenderContext> renderHandler,
            Consumer<? super IFSlotContext> updateHandler,
            Consumer<? super IFSlotClickContext> clickHandler,
            Set<State<?>> watching,
            boolean isManagedExternally,
            boolean updateOnClick,
            boolean isVisible) {
        this.root = root;
        this.position = position;
        this.stack = stack;
        this.cancelOnClick = cancelOnClick;
        this.closeOnClick = closeOnClick;
        this.displayCondition = displayCondition;
        this.renderHandler = renderHandler;
        this.updateHandler = updateHandler;
        this.clickHandler = clickHandler;
        this.watching = watching;
        this.isManagedExternally = isManagedExternally;
        this.updateOnClick = updateOnClick;
        this.isVisible = isVisible;
    }

    @Override
    public String getKey() {
        return key;
    }

    @NotNull
    @Override
    public VirtualView getRoot() {
        return root;
    }

    @Override
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

    public Consumer<? super IFSlotRenderContext> getRenderHandler() {
        return renderHandler;
    }

    public Consumer<? super IFSlotContext> getUpdateHandler() {
        return updateHandler;
    }

    public Consumer<? super IFSlotClickContext> getClickHandler() {
        return clickHandler;
    }

    public Set<State<?>> getWatching() {
        return watching;
    }

    @Override
    public boolean isContainedWithin(int position) {
        return getPosition() == position;
    }

    @Override
    public boolean intersects(@NotNull Component other) {
        return Component.intersects(this, other);
    }

    @Override
    public @NotNull InteractionHandler getInteractionHandler() {
        return this;
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        if (getRenderHandler() != null) {
            final int initialSlot = getPosition();
            getRenderHandler().accept(context);

            // Externally managed components have its own displacement measures
            if (!isManagedExternally()) {
                final int updatedSlot = context.getSlot();
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

            context.getContainer().renderItem(getPosition(), context.getResult());
            setVisible(true);
            return;
        }

        if (getStack() == null) {
            throw new IllegalStateException("At least one fallback item or render handler must be provided");
        }

        context.getContainer().renderItem(getPosition(), getStack());
        setVisible(true);
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        if (context.isCancelled()) return;

        // Static item with no `displayIf` must not even reach the update handler
        if (displayCondition == null && getRenderHandler() == null) return;

        if (getUpdateHandler() != null) {
            getUpdateHandler().accept(context);
            if (context.isCancelled()) return;
        }

        context.getParent().renderComponent(this);
    }

    @Override
    public void clear(@NotNull IFContext context) {
        ((IFRenderContext) context).getContainer().removeItem(getPosition());
    }

    @Override
    public void update() {
        if (isManagedExternally())
            throw new IllegalStateException(
                    "This component is externally managed by another component and cannot be updated directly");

        if (root instanceof IFContext) ((IFContext) root).updateComponent(this);
    }

    @Override
    public @UnmodifiableView Set<State<?>> getWatchingStates() {
        return Collections.unmodifiableSet(getWatching());
    }

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {
        if (getClickHandler() != null) getClickHandler().accept(context);
        if (isUpdateOnClick()) context.update();
    }

    @Override
    public boolean isVisible() {
        if (root instanceof Component) return ((Component) root).isVisible() && isVisible;

        return isVisible;
    }

    @Override
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    @Override
    public boolean isManagedExternally() {
        return isManagedExternally;
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
                + clickHandler + ", watching="
                + watching + ", isVisible="
                + isVisible + ", isManagedExternally="
                + isManagedExternally + '}';
    }
}
