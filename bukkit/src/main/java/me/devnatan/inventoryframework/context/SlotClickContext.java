package me.devnatan.inventoryframework.context;

import lombok.Getter;
import lombok.Setter;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotClickContext extends SlotContext implements IFSlotClickContext {

    @NotNull
    private final InventoryClickEvent clickOrigin;

    @Getter
    @Setter
    private boolean cancelled;

    public SlotClickContext(
            @NotNull RootView root,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            int slot,
            @NotNull IFContext parent,
            @Nullable IFItem<?> internalItem,
            @NotNull InventoryClickEvent clickOrigin) {
        super(root, container, viewer, slot, parent, internalItem);
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

    @Override
    public final boolean isOnEntityContainer() {
        return getClickOrigin().getClickedInventory() instanceof PlayerInventory;
    }
}
