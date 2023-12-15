package me.devnatan.inventoryframework.context;

import java.util.List;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class PublicComponentRenderContext
        extends PublicPlatformComponentRenderContext<
                PublicComponentRenderContext, BukkitItemComponentBuilder, ItemStack>
        implements Context {

    @ApiStatus.Internal
    public PublicComponentRenderContext(IFComponentRenderContext componentContext) {
        super(componentContext);
    }

    private ComponentRenderContext delegate() {
        return (ComponentRenderContext) getConfinedContext();
    }

    public ItemStack getItem() {
        return delegate().getItem();
    }

    public void setItem(ItemStack item) {
        delegate().setItem(item);
    }

    public int getSlot() {
        return delegate().getSlot();
    }

    public void setSlot(int slot) {
        delegate().setSlot(slot);
    }

    public void setSlot(int row, int column) {
        delegate().setSlot(row, column);
    }

    @Override
    protected BukkitItemComponentBuilder createItemBuilder() {
        return new BukkitItemComponentBuilder().withSelfManaged(true);
    }

    @Override
    public String toString() {
        return "PublicPlatformRenderContext{} " + super.toString();
    }

    @Override
    public @UnknownNullability Player getPlayer() {
        return delegate().getPlayer();
    }

    @Override
    public List<Player> getAllPlayers() {
        return delegate().getAllPlayers();
    }

    @Override
    public void updateTitleForPlayer(@NotNull String title, @NotNull Player player) {
        delegate().updateTitleForPlayer(title, player);
    }

    @Override
    public void resetTitleForPlayer(@NotNull Player player) {
        delegate().resetTitleForPlayer(player);
    }
}
