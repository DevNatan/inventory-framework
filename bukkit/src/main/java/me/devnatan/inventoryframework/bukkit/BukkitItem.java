package me.devnatan.inventoryframework.bukkit;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.Getter;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
public final class BukkitItem extends IFItem<BukkitItem> {

    private Consumer<? super IFSlotRenderContext> renderHandler;
    private Consumer<? super IFSlotContext> updateHandler;
    private Consumer<? super IFSlotClickContext> clickHandler;
    private Consumer<? super IFSlotClickContext> holdHandler;
    private BiConsumer<? super IFSlotClickContext, ? super IFSlotClickContext> releaseHandler;

    @ApiStatus.Internal
    public BukkitItem(int slot) {
        super(slot);
    }

    @Override
    public void render(@NotNull IFSlotRenderContext context) {
        super.render(context);

        getRenderHandler().accept(context);
        context.getContainer().renderItem(getPosition(), ((SlotRenderContext) context).getItem());
    }

    /**
     * Defines the item that will be used as fallback for rendering in the slot where this item is
     * positioned. The fallback item is always static.
     *
     * <p>The function of the fallback item is to provide an alternative if the item's rendering
     * functions are not quenched, thus returning the rendering to the fallback item.
     *
     * <pre>{@code
     * slot(30)
     * 	   .withItem(...)
     *     .onRender(render -> {
     *         render.setItem(someCondition ? null : item);
     *     })
     *     .onUpdate(update -> {
     *         update.setItem(someCondition ? null : item);
     *     });
     * }</pre>
     *
     * <p>If neither of the above two conditions are satisfied, the fallback item will be rendered,
     * otherwise the item defined in the handlers will be rendered.
     *
     * @param fallbackItem The new fallback item stack.
     * @return This item.
     */
    public BukkitItem item(ItemStack fallbackItem) {
        super.setItem(fallbackItem);
        return this;
    }

    /**
     * Defines the item that will be used as fallback for rendering in the slot where this item is
     * positioned. The fallback item is always static.
     *
     * <p>The function of the fallback item is to provide an alternative if the item's rendering
     * functions are not quenched, thus returning the rendering to the fallback item.
     *
     * <pre>{@code
     * slot(30)
     * 	   .withItem(...)
     *     .onRender(render -> {
     *         render.setItem(someCondition ? null : item);
     *     })
     *     .onUpdate(update -> {
     *         update.setItem(someCondition ? null : item);
     *     });
     * }</pre>
     *
     * <p>If neither of the above two conditions are satisfied, the fallback item will be rendered,
     * otherwise the item defined in the handlers will be rendered.
     *
     * @param fallbackItem The new fallback item stack.
     * @return This item.
     */
    public BukkitItem item(Material fallbackItem) {
        super.setItem(new ItemStack(fallbackItem));
        return this;
    }

    /**
     * Called when the item is rendered.
     *
     * <p>This handler is called every time the item or the view that owns it is updated.
     *
     * <p>It is allowed to change the item that will be displayed in this handler using the context
     * mutation functions.
     *
     * <p>An item can be re-rendered individually using {@link IFSlotContext#updateSlot()}.
     *
     * @param renderHandler The render handler.
     * @return This item.
     */
    public BukkitItem onRender(@Nullable Consumer<? super SlotRenderContext> renderHandler) {
        this.renderHandler = (Consumer<? super IFSlotRenderContext>) renderHandler;
        return this;
    }

    /**
     * Called when the item is rendered.
     *
     * <p>This handler is called every time the item or the view that owns it is updated.
     *
     * <p>It is allowed to change the item that will be displayed in this handler using the context
     * mutation functions.
     *
     * <p>An item can be re-rendered individually using {@link IFSlotContext#updateSlot()}.
     *
     * @param renderHandler The render handler.
     * @return This item.
     */
    public BukkitItem rendered(@Nullable Function<SlotRenderContext, ItemStack> renderHandler) {
        return renderHandler == null
                ? this
                : onRender(renderContext -> renderContext.setItem(renderHandler.apply(renderContext)));
    }

    /**
     * Called when the item is rendered.
     *
     * <p>This handler is called every time the item or the view that owns it is updated.
     *
     * <p>It is allowed to change the item that will be displayed in this handler using the context
     * mutation functions.
     *
     * <p>An item can be re-rendered individually using {@link IFSlotContext#updateSlot()}.
     *
     * @param renderHandler The render handler.
     * @return This item.
     */
    public BukkitItem rendered(@Nullable Supplier<ItemStack> renderHandler) {
        return renderHandler == null ? this : onRender(renderContext -> renderContext.setItem(renderHandler.get()));
    }

    /**
     * Called when the item is updated.
     *
     * <p>It is allowed to change the item that will be displayed in this handler using the context
     * mutation functions.
     *
     * <p>An item can be updated individually using {@link IFSlotContext#updateSlot()}.
     *
     * @param updateHandler The update handler.
     * @return This item.
     */
    public BukkitItem onUpdate(@Nullable Consumer<? super SlotContext> updateHandler) {
        this.updateHandler = (Consumer<? super IFSlotContext>) updateHandler;
        return this;
    }

    /**
     * Called when a player clicks on the item.
     *
     * <p>This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * <p>**Using item mutation functions in this handler is not allowed.**
     *
     * @param clickHandler The click handler.
     * @return This item.
     */
    public BukkitItem onClick(@Nullable Consumer<? super SlotClickContext> clickHandler) {
        this.clickHandler = (Consumer<? super IFSlotClickContext>) clickHandler;
        return this;
    }

    /**
     * Called when a player clicks on the item.
     *
     * <p>This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * <p>**Using item mutation functions in this handler is not allowed.**
     *
     * @param clickHandler The click handler.
     * @return This item.
     */
    public BukkitItem onClick(@Nullable Runnable clickHandler) {
        this.clickHandler = clickHandler == null ? null : $ -> clickHandler.run();
        return this;
    }

    /**
     * Called when a player holds an item.
     *
     * <p>This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * <p>You can check if the item has been released using {@link #onRelease(BiConsumer)}.
     *
     * <p>**Using item mutation functions in this handler is not allowed.**
     *
     * @param holdHandler The item hold handler.
     * @return This item.
     */
    public BukkitItem onHold(@Nullable Consumer<? super SlotClickContext> holdHandler) {
        this.holdHandler = (Consumer<? super IFSlotClickContext>) holdHandler;
        return this;
    }

    /**
     * Called when a player releases an item.
     *
     * <p>This handler works on any container that the actor has access to and only works if the
     * interaction has not been cancelled.
     *
     * <p>You can know when the item was hold using {@link #onHold(Consumer)}.
     *
     * <p>**Using item mutation functions in this handler is not allowed.**
     *
     * @param releaseHandler The item release handler.
     * @return This item.
     */
    public BukkitItem onRelease(
            @Nullable BiConsumer<? super SlotClickContext, ? super SlotClickContext> releaseHandler) {
        this.releaseHandler = (BiConsumer<? super IFSlotClickContext, ? super IFSlotClickContext>) releaseHandler;
        return this;
    }
}
