package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;

public class ViewItem {

    private final int slot;
    private ItemStack item;

    // fast path handlers
    private boolean closeOnClick;
    private boolean cancelOnClick;

    private ViewItemHandler clickHandler;
    private ViewItemHandler renderHandler;
    private ViewItemHandler updateHandler;

    public ViewItem() {
        this(0);
    }

    public ViewItem(int slot) {
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public ViewItem withItem(ItemStack item) {
        this.item = item;
        return this;
    }

    public ViewItemHandler getClickHandler() {
        return clickHandler;
    }

    public ViewItemHandler getRenderHandler() {
        return renderHandler;
    }

    public ViewItemHandler getUpdateHandler() {
        return updateHandler;
    }

    public ViewItem onClick(ViewItemHandler clickHandler) {
        this.clickHandler = clickHandler;
        return this;
    }

    public ViewItem onRender(ViewItemHandler renderHandler) {
        this.renderHandler = renderHandler;
        return this;
    }

    public ViewItem onUpdate(ViewItemHandler updateHandler) {
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
        cancelOnClick = true;
        closeOnClick = true;
        return this;
    }

    public ViewItem cancelOnClick() {
        cancelOnClick = true;
        return this;
    }

    @Override
    public String toString() {
        return "ViewItem{" +
                "slot=" + slot +
                ", item=" + item +
                ", closeOnClick=" + closeOnClick +
                ", cancelOnClick=" + cancelOnClick +
                '}';
    }

}
