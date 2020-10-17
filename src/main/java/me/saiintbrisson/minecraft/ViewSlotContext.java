package me.saiintbrisson.minecraft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ViewSlotContext extends ViewContext {

    private final int slot;
    private ItemStack item;

    public ViewSlotContext(View view, Player player, Inventory inventory, int slot, ItemStack item) {
        super(view, player, inventory);
        this.slot = slot;
        this.item = item.clone();
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public boolean hasItem() {
        return item != null;
    }

    public boolean isEmpty() {
        return hasItem() && item.getType() == Material.AIR;
    }

    public void setItemDisplayName(String displayName) {
        if (item == null || !item.hasItemMeta())
            return;

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
    }

    public void updateSlot() {
        getView().update(getPlayer(), slot);
    }

}