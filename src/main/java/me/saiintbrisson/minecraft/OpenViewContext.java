package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import static me.saiintbrisson.minecraft.View.INVENTORY_ROW_SIZE;

/**
 * This context happens before the inventory is opened, it is used for cancellation by previously defined data,
 * for any reason and can be used to change the title and size of the inventory before the rendering intent.
 */
public final class OpenViewContext extends ViewContext {

	private String inventoryTitle;
	private int inventorySize;

	public OpenViewContext(@NotNull View view, @NotNull Player player) {
		super(view, player, view.getLastSlot() + 1);
		inventorySize = view.getItems().length;
	}

	@Override
	public Inventory getInventory() {
		throw new IllegalStateException(
			"It is not allowed to manipulate or try to get the inventory instance in the initial open handler " +
			"because the inventory has not yet been created. If you really need it, use `onRender` instead."
		);
	}

	/**
	 * Returns the custom inventory title if it has been defined.
	 */
	public String getInventoryTitle() {
		return inventoryTitle;
	}

	/**
	 * Defines the title of the inventory that will be created
	 * for the new specified title according to the context.
	 *
	 * @param title the new inventory title.
	 */
	public void setInventoryTitle(String title) {
		Preconditions.checkNotNull(title, "Inventory title cannot be null");
		this.inventoryTitle = title;
	}

	/**
	 * Returns the size of the inventory.
	 */
	public int getInventorySize() {
		return inventorySize;
	}

	/**
	 * Defines the new size for the inventory.
	 *
	 * @param inventorySize the new inventory size.
	 */
	public void setInventorySize(int inventorySize) {
		// is less than nine, probably the person thought it was to put the row so we convert at once
		this.inventorySize = inventorySize < INVENTORY_ROW_SIZE ? inventorySize * INVENTORY_ROW_SIZE : inventorySize;
	}

	@Override
	protected void inventoryModificationTriggered() {
		throw new IllegalStateException(
			"It is not allowed to modify the inventory " +
			"in the opening context as the inventory was not even created. " +
			"Use the onRender() rendering function for this."
		);
	}

	@Override
	public String toString() {
		return "OpenViewContext{" +
			"inventoryTitle='" + inventoryTitle + '\'' +
			", inventorySize=" + inventorySize +
			"} " + super.toString();
	}

}
