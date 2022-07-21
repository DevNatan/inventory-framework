package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class BukkitClickViewSlotContext extends AbstractViewSlotContext implements ViewSlotClickContext {

    private final InventoryClickEvent clickOrigin;

    BukkitClickViewSlotContext(
            final ViewItem backingItem,
            @NotNull final BaseViewContext parent,
            @NotNull final InventoryClickEvent clickOrigin) {
        super(backingItem, parent);
        this.clickOrigin = clickOrigin;
    }

    @Override
    public final int getSlot() {
        return getClickOrigin().getSlot();
    }

    @Override
    public final @NotNull Player getPlayer() {
        return (Player) clickOrigin.getWhoClicked();
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
    public final boolean isOnEntityContainer() {
        return getClickOrigin().getClickedInventory() instanceof PlayerInventory;
    }

    @Override
    public final boolean isOutsideClick() {
        return getClickOrigin().getSlotType() == InventoryType.SlotType.OUTSIDE;
    }

    @Override
    public @NotNull String getClickIdentifier() {
        return getClickOrigin().getClick().name();
    }

    @Override
    public final void inventoryModificationTriggered() {
        throw new IllegalStateException("You cannot update the item in the click handler context. "
                + "Use the slot.onRender(...) or slot.onUpdate(...) and then context.setItem(...) instead.");
    }
}
