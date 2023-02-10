package me.devnatan.inventoryframework.context;

import lombok.Getter;
import me.devnatan.inventoryframework.BukkitItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.ViewConfigBuilder;
import me.devnatan.inventoryframework.internal.BukkitViewer;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@Getter
public final class RenderContext extends ConfinedContext implements IFRenderContext<BukkitItem> {

    @NotNull
    private final Player player;

    private final ViewConfigBuilder inheritedConfigBuilder = new ViewConfigBuilder();

    @ApiStatus.Internal
    public RenderContext(@NotNull RootView root, @NotNull ViewContainer container, @NotNull Viewer viewer) {
        super(root, container, viewer);
        this.player = ((BukkitViewer) viewer).getPlayer();
    }

    @Override
    public @NotNull BukkitItem layoutSlot(String character) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull BukkitItem slot(int slot) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull BukkitItem slot(int row, int column) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull BukkitItem firstSlot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull BukkitItem lastSlot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull BukkitItem availableSlot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public @NotNull ViewConfigBuilder config() {
        return inheritedConfigBuilder;
    }
}
