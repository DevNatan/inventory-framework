package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString(callSuper = true)
class BukkitClickSlotContext extends ClickContext implements ViewSlotContext {

	private final InventoryClickEvent clickOrigin;

	@Setter
	private boolean cancelled;

	BukkitClickSlotContext(
		@NotNull final ViewContext context,
		@NotNull final InventoryClickEvent clickOrigin
	) {
		super(context, clickOrigin);
		this.clickOrigin = clickOrigin;
	}

	@Override
	public int getSlot() {
		return clickOrigin.getSlot();
	}

}
