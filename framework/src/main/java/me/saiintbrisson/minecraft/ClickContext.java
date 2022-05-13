package me.saiintbrisson.minecraft;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@ToString(callSuper = true)
class ClickContext extends BaseViewContext {

	ClickContext(@NotNull final ViewContext context, @NotNull InventoryClickEvent clickOrigin) {
		super(context);
	}

	@Override
	final void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"You cannot modify the inventory in the click handler context. " +
				"Use the onRender(...) rendering function for this."
		);
	}

}
