package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@Setter
@ToString(callSuper = true)
public final class BukkitClickViewSlotContext extends BaseViewContext implements ViewSlotContext {

	private final ViewContext parent;
	private final InventoryClickEvent clickOrigin;

	@Setter
	private boolean cancelled;

	BukkitClickViewSlotContext(
		@NotNull final ViewContext parent,
		@NotNull final InventoryClickEvent clickOrigin
	) {
		super(parent.getRoot(), parent.getContainer());
		this.parent = parent;
		this.clickOrigin = clickOrigin;
	}

	@Override
	public @NotNull ViewContextAttributes getAttributes() {
		return parent.getAttributes();
	}

	@Override
	public int getSlot() {
		return clickOrigin.getSlot();
	}

	@Override
	public ViewItem withItem(@Nullable Object fallbackItem) {
		return null;
	}

	@Override
	public Player getPlayer() {
		return BukkitViewer.toPlayerOfContext(this);
	}

	@Override
	void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"You cannot modify the inventory directly in the click handler context. " +
				"Use the onRender(...) and then context.setItem(...) instead."
		);
	}

}
