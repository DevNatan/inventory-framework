package me.devnatan.inventoryframework.context;

import java.util.Map;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SlotRenderContext extends SlotContext implements IFSlotRenderContext {

    private ItemStack item;
    private boolean cancelled;

    public SlotRenderContext(
            @NotNull RootView root,
            @NotNull ViewContainer container,
            Viewer subject,
            @NotNull Map<String, Viewer> viewers,
            int slot,
            @NotNull IFContext parent,
            @Nullable Component component) {
        super(root, container, subject, viewers, slot, parent, component);
    }

    @Override
    public Object getResult() {
        return item;
    }

    @Override
    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public String toString() {
        return "SlotRenderContext{" + "item=" + item + ", cancelled=" + cancelled + "} " + super.toString();
    }
}
