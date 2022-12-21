package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.IFSlotClickContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

/**
 * Bukkit platform ViewSlotClickContext implementation with click origin property.
 */
@Getter
@Setter
@ToString(callSuper = true, onlyExplicitlyIncluded = true)
public class BukkitClickViewSlotContext extends BukkitViewSlotContext implements IFSlotClickContext {

    private final InventoryClickEvent clickOrigin;

    BukkitClickViewSlotContext(
            int slot,
            @NotNull InventoryClickEvent clickOrigin,
            ViewItem backingItem,
            IFContext parent,
            ViewContainer container) {
        super(slot, backingItem, parent, container);
        this.clickOrigin = clickOrigin;
        this.item = new ItemWrapper(clickOrigin.getCurrentItem());
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
