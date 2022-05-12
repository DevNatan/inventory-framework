package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString(callSuper = true)
class ClickSlotContext extends BaseViewContext implements ViewSlotContext {

	private final InventoryClickEvent clickOrigin;

	@Setter
	private boolean cancelled;

	public ClickSlotContext(
		@NotNull ViewContext context,
		@NotNull InventoryClickEvent clickOrigin
	) {
		super(context);
		this.clickOrigin = clickOrigin;
	}

	@Override
	void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"You cannot modify the inventory in the click context. " +
			"Use the onRender(...) rendering function for this."
		);
	}

}
