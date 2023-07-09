package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

public class ItemComponent implements Component, InteractionHandler {

    private final VirtualView root;
    private final int position;
    private final Object stack;
    private final boolean cancelOnClick;
    private final boolean closeOnClick;
    private final BooleanSupplier shouldRender;
    private final Consumer<? super IFSlotRenderContext> renderHandler;
    private final Consumer<? super IFSlotContext> updateHandler;
    private final Consumer<? super IFSlotClickContext> clickHandler;
    private final Set<State<?>> watching;

    public ItemComponent(
            VirtualView root,
            int position,
            Object stack,
            boolean cancelOnClick,
            boolean closeOnClick,
            BooleanSupplier shouldRender,
            Consumer<? super IFSlotRenderContext> renderHandler,
            Consumer<? super IFSlotContext> updateHandler,
            Consumer<? super IFSlotClickContext> clickHandler,
            Set<State<?>> watching) {
        this.root = root;
        this.position = position;
        this.stack = stack;
        this.cancelOnClick = cancelOnClick;
        this.closeOnClick = closeOnClick;
        this.shouldRender = shouldRender;
        this.renderHandler = renderHandler;
        this.updateHandler = updateHandler;
        this.clickHandler = clickHandler;
        this.watching = watching;
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

    public BooleanSupplier getShouldRender() {
        return shouldRender;
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
    public @NotNull InteractionHandler getInteractionHandler() {
        return this;
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        if (shouldRender != null && !shouldRender.getAsBoolean()) {
            context.getContainer().removeItem(getPosition());
            return;
        }

        if (renderHandler != null) {
            renderHandler.accept(context);
            context.getContainer().renderItem(getPosition(), context.getResult());
            return;
        }

        if (stack == null) {
            throw new IllegalStateException("At least one fallback item or render handler must be provided");
        }

        context.getContainer().renderItem(getPosition(), stack);
    }

    @Override
    public void updated(@NotNull IFSlotRenderContext context) {
        if (updateHandler == null) return;
        updateHandler.accept(context);
    }

    @Override
    public void clear(@NotNull IFContext context) {
        context.getContainer().removeItem(getPosition());
    }

    @Override
    public @UnmodifiableView Set<State<?>> getWatchingStates() {
        return Collections.unmodifiableSet(watching);
    }

    @Override
    public void clicked(@NotNull Component component, @NotNull IFSlotClickContext context) {
        if (clickHandler == null) return;
        clickHandler.accept(context);
    }

    @Override
    public boolean shouldBeUpdated() {
        if (shouldRender != null) return true;
        return getRenderHandler() != null;
    }

    @Override
    public boolean isVisible() {
        return ((IFContext) root).getContainer().hasItem(getPosition());
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
        return "ItemComponent{" + "position="
                + position + ", stack="
                + stack + ", cancelOnClick="
                + cancelOnClick + ", closeOnClick="
                + closeOnClick + ", shouldRender="
                + shouldRender + ", renderHandler="
                + renderHandler + ", updateHandler="
                + updateHandler + ", clickHandler="
                + clickHandler + '}';
    }
}
