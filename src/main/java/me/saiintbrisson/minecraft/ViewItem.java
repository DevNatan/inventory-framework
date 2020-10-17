package me.saiintbrisson.minecraft;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ViewItem {

    private ItemStack item;

    // fast path handlers
    private boolean closeOnClick;
    private boolean cancelOnClick;

    private ViewItemHandler<InventoryClickEvent> clickHandler;
    private ViewItemHandler<Void> renderHandler;
    private ViewItemHandler<Void> updateHandler;

    public ItemStack getItem() {
        return item;
    }

    public ViewItem withItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public ViewItemHandler<InventoryClickEvent> getClickHandler() {
        return clickHandler;
    }

    public ViewItemHandler<Void> getRenderHandler() {
        return renderHandler;
    }

    public ViewItemHandler<Void> getUpdateHandler() {
        return updateHandler;
    }

    public ViewItem onClick(ViewItemHandler<InventoryClickEvent> clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public ViewItem onRender(ViewItemHandler<Void> renderHandler) {
        this.renderHandler = renderHandler;
        return this;
    }

    public ViewItem onUpdate(ViewItemHandler<Void> updateHandler) {
        this.updateHandler = updateHandler;
        return this;
    }

    public boolean isCloseOnClick() {
        return closeOnClick;
    }

    public boolean isCancelOnClick() {
        return cancelOnClick;
    }

    public ViewItem closeOnClick() {
        closeOnClick = !closeOnClick;
        return this;
    }

    public ViewItem cancelOnClick() {
        cancelOnClick = !cancelOnClick;
        return this;
    }

}
