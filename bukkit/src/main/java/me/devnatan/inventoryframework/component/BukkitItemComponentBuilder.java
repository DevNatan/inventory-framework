package me.devnatan.inventoryframework.component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@RequiredArgsConstructor
@ToString
public final class BukkitItemComponentBuilder extends DefaultComponentBuilder<BukkitItemComponentBuilder>
        implements ItemComponentBuilder<BukkitItemComponentBuilder>, ComponentFactory {

    private final VirtualView root;

    private int slot;
    private ItemStack item;

    // --- Handlers ---
    private Consumer<? super IFSlotRenderContext> renderHandler;
    private Consumer<? super IFSlotClickContext> clickHandler;
    private Consumer<? super IFSlotContext> updateHandler;

    private BooleanSupplier shouldRender;

    @Override
    public boolean isContainedWithin(int position) {
        return position == slot;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BukkitItemComponentBuilder withSlot(int slot) {
        this.slot = slot;
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
    @SuppressWarnings("unchecked")
    public BukkitItemComponentBuilder onRender(@Nullable Consumer<? super SlotRenderContext> renderHandler) {
        this.renderHandler = (Consumer<? super IFSlotRenderContext>) renderHandler;
        return this;
    }

    /**
     * Dynamic renderization of a specific item.
     * <p>
     * This handler is called every time the item or the view that owns it is updated.
     *
     * @param renderFactory The render handler.
     * @return This item builder.
     */
    public BukkitItemComponentBuilder renderWith(@NotNull Supplier<@Nullable ItemStack> renderFactory) {
        return onRender(render -> render.setItem(renderFactory.get()));
    }

    /**
     * Only renders this item if the render condition is satisfied.
     * <p>
     * It's a help function to simplify the use with other things like {@link Pagination}.
     * <pre>{@code
     * // This example only renders the arrow if pagination can advance
     * render.layoutSlot('>')
     *     .renderWith(() -> new ItemStack(Material.ARROW))
     *     .renderIf(pagination::canAdvance)
     * }</pre>
     * <p>
     * This method overwrites {@link #onRender(Consumer)} when the item set is null.
     *
     * @param renderCondition The renderization condition.
     * @return This item builder.
     */
    public BukkitItemComponentBuilder renderIf(BooleanSupplier renderCondition) {
        this.shouldRender = renderCondition;
        return this;
    }

    /**
     * Called when a player clicks on the item.
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
     * Called when the item is updated.
     *
     * @param updateHandler The update handler.
     * @return This item builder.
     */
    @SuppressWarnings("unchecked")
    public BukkitItemComponentBuilder onUpdate(@Nullable Consumer<? super SlotContext> updateHandler) {
        this.updateHandler = (Consumer<? super IFSlotContext>) updateHandler;
        return this;
    }

    @Override
    public @NotNull Component create() {
        return new ItemComponent(
                root,
                slot,
                item,
                isCancelOnClick(),
                isCloseOnClick(),
                shouldRender,
                renderHandler,
                updateHandler,
                clickHandler,
                getWatching());
    }
}
