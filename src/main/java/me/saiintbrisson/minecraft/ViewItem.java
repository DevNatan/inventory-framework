package me.saiintbrisson.minecraft;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.saiintbrisson.minecraft.View.UNSET_SLOT;

public class ViewItem {

    public enum State {

        UNDEFINED,

        HOLDING,

    }

    private final int slot;
    private ItemStack item;

    // fast path handlers
    private boolean closeOnClick;
    private boolean cancelOnClick;
    private boolean overrideCancelOnClick;
    private boolean preserveSlotSwapState = true;

    private ViewItemHandler clickHandler;
    private ViewItemHandler renderHandler;
    private ViewItemHandler updateHandler;

    private Map<String, Object> staticData;
    private State state = State.UNDEFINED;

    public ViewItem() {
        this(UNSET_SLOT);
    }

    public ViewItem(int slot) {
        this.slot = slot;
    }

    State getState() {
        return state;
    }

    void setState(State state) {
        this.state = state;
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

    public boolean isOverrideCancelOnClick() {
        return overrideCancelOnClick;
    }

    public boolean isCancelOnClick() {
        return cancelOnClick;
    }

    public void setCancelOnClick(boolean cancelOnClick) {
        this.cancelOnClick = cancelOnClick;
        overrideCancelOnClick = true;
    }

    public ViewItem withCancelOnClick(boolean cancelOnClick) {
        setCancelOnClick(cancelOnClick);
        return this;
    }

    public ViewItem cancelOnClick() {
        return withCancelOnClick(true);
    }

    public boolean isPreserveSlotSwapState() {
        return preserveSlotSwapState;
    }

    public ViewItem withPreserveSlotSwapState(boolean preserveSlotSwapState) {
        this.preserveSlotSwapState = preserveSlotSwapState;
        return this;
    }

    Object getData(String key) {
        return staticData == null ? null : staticData.get(key);
    }

    public ViewItem withData(String key, Object value) {
        if (staticData == null)
            staticData = new HashMap<>();

        staticData.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "ViewItem{" +
                "slot=" + slot +
                ", item=" + item +
                ", closeOnClick=" + closeOnClick +
                ", cancelOnClick=" + cancelOnClick +
                ", overrideCancelOnClick=" + overrideCancelOnClick +
                ", preserveSlotSwapState=" + preserveSlotSwapState +
                ", clickHandler=" + clickHandler +
                ", renderHandler=" + renderHandler +
                ", updateHandler=" + updateHandler +
                ", staticData=" + staticData +
                '}';
    }

}
