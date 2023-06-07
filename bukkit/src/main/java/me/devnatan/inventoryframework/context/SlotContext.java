package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.ToString;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.runtime.BukkitViewContainer;
import me.devnatan.inventoryframework.runtime.BukkitViewer;
import me.devnatan.inventoryframework.state.State;
import me.devnatan.inventoryframework.state.StateValue;
import me.devnatan.inventoryframework.state.StateWatcher;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.Internal
@ToString(callSuper = true)
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
        super(root, container, viewer, parent.getInitialData());
        this.slot = slot;
        this.player = ((BukkitViewer) viewer).getPlayer();
        this.parent = parent;
        this.component = component;
    }

    @Override
    public @NotNull ViewContainer getContainer() {
        return getParent().getContainer();
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Viewer> getIndexedViewers() {
        return getParent().getIndexedViewers();
    }

    @Override
    public final @NotNull UUID getId() {
        return getParent().getId();
    }

    @Override
    public final @NotNull ViewConfig getConfig() {
        return getParent().getConfig();
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
    public final @NotNull IFContext getParent() {
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

    @Override
    public final @UnmodifiableView @NotNull List<Component> getComponents() {
        return getParent().getComponents();
    }

    @Override
    public final void addComponent(@NotNull Component component) {
        throw new UnsupportedOperationException("Slot context do not have components");
    }

    @Override
    public final void removeComponent(@NotNull Component component) {
        throw new UnsupportedOperationException("Slot context do not have components");
    }

    @Override
    public boolean isMarkedForRemoval(int componentIndex) {
        return getParent().isMarkedForRemoval(componentIndex);
    }

    @Override
    public Object getState(State<?> state) {
        return getParent().getState(state);
    }

    @Override
    public void initState(long id, @NotNull StateValue value, Object initialValue) {
        getParent().initState(id, value, initialValue);
    }

    @Override
    public void updateState(long id, Object value) {
        getParent().updateState(id, value);
    }

    @Override
    public void watchState(long id, StateWatcher listener) {
        getParent().watchState(id, listener);
    }
}
