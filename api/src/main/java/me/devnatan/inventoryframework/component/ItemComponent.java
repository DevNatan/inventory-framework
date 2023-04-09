package me.devnatan.inventoryframework.component;

import java.util.Collections;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.state.State;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ApiStatus.NonExtendable
public class ItemComponent implements Component, InteractionHandler {

    @ToString.Exclude
    private final VirtualView root;

    @EqualsAndHashCode.Include
    private final int position;

    @EqualsAndHashCode.Include
    private final Object stack;

    private final boolean cancelOnClick;
    private final boolean closeOnClick;
    private final BooleanSupplier shouldRender;
    private final Consumer<? super IFSlotRenderContext> renderHandler;
    private final Consumer<? super IFSlotContext> updateHandler;
    private final Consumer<? super IFSlotClickContext> clickHandler;

    @ToString.Exclude
    private final Set<State<?>> watching;

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
}
