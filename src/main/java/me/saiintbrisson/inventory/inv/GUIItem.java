package me.saiintbrisson.inventory.inv;

import lombok.Getter;
import me.saiintbrisson.inventory.paginator.PaginatedItem;
import me.saiintbrisson.inventory.paginator.PaginatedView;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

@Getter
public class GUIItem<T> {

    private int slot;
    private ItemStack itemStack;
    private final List<GUIAction<T, InventoryClickEvent>> clickActions = new LinkedList<>();

    public GUIItem<T> withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public GUIItem<T> withSlot(int row, int column) {
        return withSlot((row * 9) + column);
    }

    public GUIItem<T> onClick(GUIAction<T, InventoryClickEvent> clickAction) {
        clickActions.add(clickAction);
        return this;
    }

    public GUIItem<T> cancelClick() {
        clickActions.add((node, player, event) -> event.setCancelled(true));
        return this;
    }

    public GUIItem<T> closeOnClick() {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.closeInventory();
        });

        return this;
    }

    public <N> GUIItem<T> openGUI(GUI<N> gui, N object) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.closeInventory();
            gui.createNode(player, object).show();
        });

        return this;
    }

    public <N extends PaginatedItem> GUIItem<T> openPaginatedView(PaginatedView<N> view) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.closeInventory();
            view.showInventory(player);
        });

        return this;
    }

    public GUIItem<T> updateOnClick() {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            node.update();
        });

        return this;
    }

    public GUIItem<T> messageOnClick(String message, Object... objects) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.sendMessage(String.format(message, objects));
        });

        return this;
    }

    public GUIItem<T> playSoundOnClick(Sound sound, float volume, float pitch) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.playSound(player.getLocation(), sound, volume, pitch);
        });

        return this;
    }

    public GUIItem<T> withItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public void handleClick(GUINode<T> node, InventoryClickEvent event) {
        for(GUIAction<T, InventoryClickEvent> action : clickActions) {
            action.interact(node, ((Player) event.getWhoClicked()), event);
        }
    }

}
