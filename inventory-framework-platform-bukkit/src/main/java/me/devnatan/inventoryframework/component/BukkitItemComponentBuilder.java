package me.devnatan.inventoryframework.component;

import java.util.function.Consumer;
import java.util.function.Supplier;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.IFComponentRenderContext;
import me.devnatan.inventoryframework.context.IFComponentUpdateContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitItemComponentBuilder extends PlatformComponentBuilder<BukkitItemComponentBuilder, Context>
        implements ItemComponentBuilder<BukkitItemComponentBuilder, ItemStack> {

    private int slot;
    private ItemStack item;

    // TODO Use platform-specific Render and Update component context
    private Consumer<? super IFComponentRenderContext> renderHandler;
    private Consumer<? super IFComponentUpdateContext> updateHandler;
    private Consumer<? super SlotClickContext> clickHandler;

    @Override
    public String toString() {
        return "BukkitItemComponentBuilder{" + "slot=" + slot + ", item=" + item + "} " + super.toString();
    }

    @Override
    public boolean isContainedWithin(int position) {
        return position == slot;
    }

    @Override
    public BukkitItemComponentBuilder withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    @Override
    public BukkitItemComponentBuilder withSlot(int row, int column) {
        // FIXME Missing root availability, root must be available
        // final ViewContainer container = ((IFRenderContext) root).getContainer();
        // return withSlot(SlotConverter.convertSlot(row, column, container.getRowsCount(),
        // container.getColumnsCount()));
        return this;
    }

    /**
     * Defines the item that will be used as fallback for rendering in the slot where this item is
     * positioned. The fallback item is always static.
     *
     * @param item The new fallback item stack.
     * @return This item builder.
     */
    public BukkitItemComponentBuilder withItem(@Nullable ItemStack item) {
        this.item = item;
        return this;
    }

    /**
     * Called when the item is rendered.
     * <p>
     * This handler is called every time the item or the view that owns it is updated.
     *
     * @param renderHandler The render handler.
     * @return This item builder.
     */
    public BukkitItemComponentBuilder onRender(@Nullable Consumer<? super IFComponentRenderContext> renderHandler) {
        this.renderHandler = renderHandler;
        return this;
    }

    /**
     * Dynamic rendering of a specific item.
     * <p>
     * This handler is called every time the item or the view that owns it is updated.
     *
     * @param renderFactory The render handler.
     * @return This item builder.
     */
    public BukkitItemComponentBuilder renderWith(@NotNull Supplier<@Nullable ItemStack> renderFactory) {
        // FIXME Missing implementation
        return this;
        // return onRender(render -> render.setItem(renderFactory.get()));
    }

    /**
     * Called when a player clicks on the component.
     * <p>
     * This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * @param clickHandler The click handler.
     * @return This item builder.
     */
    @SuppressWarnings("unchecked")
    public BukkitItemComponentBuilder onClick(@Nullable Consumer<? super SlotClickContext> clickHandler) {
        this.clickHandler = (Consumer<? super IFSlotClickContext>) clickHandler;
        return this;
    }

    /**
     * Called when a player clicks on the component.
     * <p>
     * This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * @param clickHandler The click handler.
     * @return This item builder.
     */
    public BukkitItemComponentBuilder onClick(@Nullable Runnable clickHandler) {
        return onClick(clickHandler == null ? null : $ -> clickHandler.run());
    }

    /**
     * Called when the item is updated.
     *
     * @param updateHandler The update handler.
     * @return This item builder.
     */
    @SuppressWarnings("unchecked")
    public BukkitItemComponentBuilder onUpdate(@Nullable Consumer<? super SlotContext> updateHandler) {
        this.updateHandler = (Consumer<? super IFComponentUpdateContext>) updateHandler;
        return this;
    }

    @Override
    public Component build(VirtualView root) {
        return new BukkitItemComponentImpl(
                slot,
                item,
                key,
                root,
                reference,
                watchingStates,
                displayCondition,
                renderHandler,
                updateHandler,
                (Consumer<? super IFSlotClickContext>) clickHandler,
                cancelOnClick,
                closeOnClick,
                updateOnClick);
    }
}
