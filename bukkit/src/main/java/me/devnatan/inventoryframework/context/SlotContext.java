package me.devnatan.inventoryframework.context;

import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewContainer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import me.devnatan.inventoryframework.component.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class SlotContext extends ConfinedContext implements IFSlotContext, Context {

    private final int slot;
    private final Player player;
    private final IFContext parent;
    private final Component component;

    public SlotContext(
            @NotNull RootView root,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            int slot,
            @NotNull IFContext parent,
            @Nullable Component component) {
        super(root, container, viewer);
        this.slot = slot;
        this.player = ((BukkitViewer) viewer).getPlayer();
        this.parent = parent;
        this.component = component;
    }

    @Override
    public final int getSlot() {
        return slot;
    }

    @Override
    public void updateSlot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOnEntityContainer() {
        return getContainer().isEntityContainer();
    }

    @Override
    public boolean hasChanged() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setChanged(boolean changed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRegistered() {
        return false;
    }

    @Override
    public final IFContext getParent() {
        return parent;
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Component getComponent() {
        return component;
    }

    @Override
    public @NotNull Player getPlayer() {
        return player;
    }

    public ItemStack getItem() {
        return ((BukkitViewContainer) getContainer()).getInventory().getItem(getSlot());
    }

    public void setItem(@Nullable ItemStack item) {
        throw new UnsupportedOperationException();
    }
}
