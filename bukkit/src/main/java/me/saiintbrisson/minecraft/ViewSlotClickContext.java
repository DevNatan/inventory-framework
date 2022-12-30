package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.ViewItem;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ViewSlotClickContext extends ViewSlotContext implements IFSlotClickContext {

    @NotNull
    private final InventoryClickEvent clickOrigin;

    ViewSlotClickContext(
            int slot,
            ViewItem backingItem,
            @NotNull IFContext parent,
            @Nullable ViewContainer container,
            @NotNull Player player,
            @NotNull InventoryClickEvent clickOrigin) {
        super(slot, backingItem, parent, container, player);
        this.clickOrigin = clickOrigin;
    }

    /**
     * The event that triggered this context.
     * <p>
     * This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided.
     */
    @ApiStatus.Internal
    public final @NotNull InventoryClickEvent getClickOrigin() {
        return clickOrigin;
    }

    /**
     * The player who clicked on the slot.
     */
    @Override
    public final @NotNull Player getPlayer() {
        return (Player) clickOrigin.getWhoClicked();
    }

    /**
     * The item that was clicked.
     */
    @Override
    public final ItemStack getItem() {
        return clickOrigin.getCurrentItem();
    }

    @Override
    public final boolean isLeftClick() {
        return getClickOrigin().isLeftClick();
    }

    @Override
    public final boolean isRightClick() {
        return getClickOrigin().isRightClick();
    }

    @Override
    public final boolean isMiddleClick() {
        return getClickOrigin().getClick() == ClickType.MIDDLE;
    }

    @Override
    public final boolean isShiftClick() {
        return getClickOrigin().isShiftClick();
    }

    @Override
    public final boolean isKeyboardClick() {
        return getClickOrigin().getClick().isKeyboardClick();
    }

    @Override
    public final boolean isOutsideClick() {
        return getClickOrigin().getSlotType() == InventoryType.SlotType.OUTSIDE;
    }

    @Override
    @NotNull
    public final String getClickIdentifier() {
        return getClickOrigin().getClick().name();
    }
}
