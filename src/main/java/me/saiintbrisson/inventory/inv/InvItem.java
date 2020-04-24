package me.saiintbrisson.inventory.inv;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

@Getter
public class InvItem<T> {

    private int slot;
    private ItemStack itemStack;
    private final List<InvAction<T, InventoryClickEvent>> clickActions = new LinkedList<>();

    public InvItem<T> withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public InvItem<T> withSlot(int row, int column) {
        return withSlot((row * 9) + column);
    }

    public InvItem<T> onClick(InvAction<T, InventoryClickEvent> clickAction) {
        clickActions.add(clickAction);
        return this;
    }

    public InvItem<T> cancelClick() {
        clickActions.add((node, event) -> event.setCancelled(true));
        return this;
    }

    public InvItem<T> closeOnClick() {
        clickActions.add((node, event) -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
        });

        return this;
    }

    public <N> InvItem<T> openInv(Inv<N> inv, N object) {
        clickActions.add((node, event) -> {
            event.setCancelled(true);
            event.getWhoClicked().closeInventory();
            Player player = (Player) event.getWhoClicked();
            inv.createNode(player, object).show();
        });

        return this;
    }

    public InvItem<T> updateOnClick() {
        clickActions.add((node, event) -> {
            event.setCancelled(true);
            node.update();
        });

        return this;
    }

    public InvItem<T> messageOnClick(String message) {
        clickActions.add((node, event) -> {
            event.setCancelled(true);
            event.getWhoClicked().sendMessage(message);
        });

        return this;
    }

    public InvItem<T> playSoundOnClick(Sound sound, float volume, float pitch) {
        clickActions.add((node, event) -> {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();

            player.playSound(player.getLocation(), sound, volume, pitch);
        });

        return this;
    }

    public InvItem<T> withItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public void handleClick(InvNode<T> node, InventoryClickEvent event) {
        for(InvAction<T, InventoryClickEvent> action : clickActions) {
            action.interact(node, event);
        }
    }

}
