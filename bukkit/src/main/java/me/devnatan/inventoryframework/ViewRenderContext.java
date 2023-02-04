package me.devnatan.inventoryframework;

import lombok.Getter;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.internal.context.BaseViewContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@Getter
public final class ViewRenderContext extends BaseViewContext implements IFRenderContext<BukkitItem> {

    @NotNull
    private final Player player;
	private final ViewConfigBuilder inheritedConfigBuilder = new ViewConfigBuilder();

    ViewRenderContext(@NotNull ViewContext backingContext, @NotNull Player player) {
        super(backingContext.getRoot(), backingContext.getContainer());
        this.player = player;
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
