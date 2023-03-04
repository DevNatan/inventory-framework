package me.devnatan.inventoryframework.bukkit;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import lombok.Getter;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.IFSlotRenderContext;
import me.devnatan.inventoryframework.context.SlotRenderContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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

        if (getRenderHandler() == null) return;

        getRenderHandler().accept(context);

        ItemStack item = ((SlotRenderContext) context).getItem();
        context.getContainer().renderItem(getPosition(), item);
    }
}
