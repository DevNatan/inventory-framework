package me.saiintbrisson.minecraft.view;

import lombok.Getter;
import me.saiintbrisson.minecraft.paginator.PaginatedItem;
import me.saiintbrisson.minecraft.paginator.PaginatedView;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

@Getter
public class ViewItem<T> {

    private int slot;
    private ItemStack itemStack;
    private final List<ViewAction<T, InventoryClickEvent>> clickActions = new LinkedList<>();

    public ViewItem<T> withSlot(int slot) {
        this.slot = slot;
        return this;
    }

    public ViewItem<T> withSlot(int row, int column) {
        return withSlot((row * 9) + column);
    }

    public ViewItem<T> onClick(ViewAction<T, InventoryClickEvent> clickAction) {
        clickActions.add(clickAction);
        return this;
    }

    public ViewItem<T> cancelClick() {
        clickActions.add((node, player, event) -> event.setCancelled(true));
        return this;
    }

    public ViewItem<T> closeOnClick() {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.closeInventory();
        });

        return this;
    }

    public <N> ViewItem<T> openView(View<N> view, N object) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            view.createNode(object).show(player);
        });

        return this;
    }

    public <N extends PaginatedItem> ViewItem<T> openPaginatedView(PaginatedView<N> view) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.closeInventory();
            view.showInventory(player);
        });

        return this;
    }

    public ViewItem<T> updateOnClick() {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            node.update(player);
        });

        return this;
    }

    public ViewItem<T> messageOnClick(String message, Object... objects) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.sendMessage(String.format(message, objects));
        });

        return this;
    }

    public ViewItem<T> playSoundOnClick(Sound sound, float volume, float pitch) {
        clickActions.add((node, player, event) -> {
            event.setCancelled(true);
            player.playSound(player.getLocation(), sound, volume, pitch);
        });

        return this;
    }

    public ViewItem<T> withItem(ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public void handleClick(ViewNode<T> node, InventoryClickEvent event) {
        for(ViewAction<T, InventoryClickEvent> action : clickActions) {
            action.interact(node, ((Player) event.getWhoClicked()), event);
        }
    }

}
