package me.saiintbrisson.inventory.inv;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Getter
public class InvItem {

    private int slot;
    private ItemStack itemStack;
    private InvAction<InventoryClickEvent> clickAction;

    public InvItem withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public InvItem withSlot(int row, int column) {
        return withSlot((row * 9) + column);
    }

    public InvItem onClick(InvAction<InventoryClickEvent> clickAction) {
        this.clickAction = clickAction;
        return this;
    }

    public InvItem cancelClick() {
        clickAction = (node, event) -> event.setCancelled(true);
        return this;
    }

    public InvItem closeOnClick() {
        clickAction = (node, event) -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        };

        return this;
    }

    public <T> InvItem openInv(Inv<T> inv, T object) {
        clickAction = (node, event) -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            Player player = (Player) event.getWhoClicked();
            inv.createNode(player, object).show();
        };

        return this;
    }

    public InvItem updateOnClick() {
        clickAction = (node, event) -> {
            event.setCancelled(true);
            node.update();
        };

        return this;
    }

    public InvItem withItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

}
