package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.Viewer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SlotRenderContext extends SlotContext implements IFSlotRenderContext {

    private final Viewer viewer;

    private ItemStack item;
    private boolean cancelled;
    private boolean changed;

    @ApiStatus.Internal
    public SlotRenderContext(int slot, @NotNull IFRenderContext parent, @Nullable Viewer viewer) {
        super(slot, parent);
        this.viewer = viewer;
    }

    @Override
    public final Object getResult() {
        return item;
    }

    @Override
    public final ItemStack getItem() {
        return item;
    }

    public final void setItem(ItemStack item) {
        this.item = item;
        setChanged(true);
    }

    @Override
    public final boolean isCancelled() {
        return cancelled;
    }

    @Override
    public final void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public void clear() {
        setItem(null);
    }

    @Override
    public final boolean hasChanged() {
        return changed;
    }

    @Override
    public final void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public final boolean isOnEntityContainer() {
        return getContainer().isEntityContainer();
    }

    @Override
    public final Viewer getViewer() {
        return viewer;
    }

    @Override
    public final void closeForPlayer() {
        getParent().closeForPlayer();
    }

    @Override
    public final void openForPlayer(@NotNull Class<? extends RootView> other) {
        getParent().openForPlayer(other);
    }

    @Override
    public final void openForPlayer(@NotNull Class<? extends RootView> other, Object initialData) {
        getParent().openForPlayer(other, initialData);
    }

    @Override
    public final void updateTitleForPlayer(@NotNull String title) {
        getParent().updateTitleForPlayer(title);
    }

    @Override
    public final void resetTitleForPlayer() {
        getParent().resetTitleForPlayer();
    }
}
