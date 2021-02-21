package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;

import static me.saiintbrisson.minecraft.View.UNSET_SLOT;

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
        this(UNSET_SLOT);
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

    public void setCloseOnClick(boolean closeOnClick) {
        this.closeOnClick = closeOnClick;
        if (this.closeOnClick)
            setCancelOnClick(true);
    }

    public ViewItem closeOnClick() {
        return withCloseOnClick(true);
    }

    public ViewItem withCloseOnClick(boolean closeOnClick) {
        setCloseOnClick(closeOnClick);
        return this;
    }

    public boolean isCancelOnClick() {
        return cancelOnClick;
    }

    public void setCancelOnClick(boolean cancelOnClick) {
        this.cancelOnClick = cancelOnClick;
    }

    public ViewItem withCancelOnClick(boolean cancelOnClick) {
        setCancelOnClick(cancelOnClick);
        return this;
    }

    public ViewItem cancelOnClick() {
        return withCancelOnClick(true);
    }

    @Override
    public String toString() {
        return "ViewItem{" +
                "slot=" + slot +
                ", item=" + item +
                ", closeOnClick=" + closeOnClick +
                ", cancelOnClick=" + cancelOnClick +
                ", clickHandler=" + clickHandler +
                ", renderHandler=" + renderHandler +
                ", updateHandler=" + updateHandler +
                '}';
    }

}
