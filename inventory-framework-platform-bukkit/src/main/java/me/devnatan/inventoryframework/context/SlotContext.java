package me.devnatan.inventoryframework.context;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.ToString;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.state.StateValue;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@ToString(onlyExplicitlyIncluded = true)
public abstract class SlotContext extends PlatformContext implements IFSlotContext, Context {

    // --- Inherited ---
    private final IFRenderContext parent;

    // --- Properties ---
    @ToString.Include
    private int slot;

    protected SlotContext(int slot, @NotNull IFRenderContext parent) {
        this.slot = slot;
        this.parent = parent;
    }

    public abstract ItemStack getItem();

    @Override
    public final @NotNull RenderContext getParent() {
        return (RenderContext) parent;
    }

    @Override
    public final int getSlot() {
        return slot;
    }

    @Override
    public final void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public @NotNull Pipeline<IFContext> getPipeline() {
        return getParent().getPipeline();
    }

    @Override
    public final @NotNull Map<String, Viewer> getIndexedViewers() {
        return getParent().getIndexedViewers();
    }

    @Override
    public final @NotNull String getTitle() {
        return getParent().getTitle();
    }

    @Override
    public final void update() {
        getParent().update();
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
    public final @NotNull ViewContainer getContainer() {
        return getParent().getContainer();
    }

    @Override
    public final @NotNull View getRoot() {
        return getParent().getRoot();
    }

    @Override
    public final Object getInitialData() {
        return getParent().getInitialData();
    }

    @Override
    public void setInitialData(Object initialData) {
        getParent().setInitialData(initialData);
    }

    @Override
    public List<Player> getAllPlayers() {
        return getParent().getAllPlayers();
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title, @NotNull Player player) {
        getParent().updateTitleForPlayer(title, player);
    }

    @Override
    public void resetTitleForPlayer(@NotNull Player player) {
        getParent().resetTitleForPlayer(player);
    }

    @Override
    public final boolean isActive() {
        return getParent().isActive();
    }

    @Override
    public final void setActive(boolean active) {
        getParent().setActive(active);
    }

    @Override
    public final boolean isEndless() {
        return getParent().isEndless();
    }

    @Override
    public final void setEndless(boolean endless) {
        getParent().setEndless(endless);
    }

    @Override
    public final void back() {
        getParent().back();
    }

    @Override
    public final void back(Object initialData) {
        getParent().back(initialData);
    }

    @Override
    public final boolean canBack() {
        return getParent().canBack();
    }

    @Override
    public final void closeForPlayer() {
        getParent().closeForPlayer();
    }

    @Override
    public final void simulateCloseForPlayer() {
        getParent().simulateCloseForPlayer();
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

    @Override
    public final Map<Long, StateValue> getStateValues() {
        return getParent().getStateValues();
    }

    @Override
    protected final void setUpdatedTitle(String updatedTitle) {
        getParent().setUpdatedTitle(updatedTitle);
    }
}
