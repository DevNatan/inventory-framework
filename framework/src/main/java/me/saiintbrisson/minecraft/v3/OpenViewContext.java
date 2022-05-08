package me.saiintbrisson.minecraft.v3;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * This context is created before the container is opened, it is used for cancellation by previously
 * defined data, for any reason and can be used to change the title and size of the container before
 * the rendering intent.
 */
@Getter
@ToString
public final class OpenViewContext extends BaseViewContext {

	/**
	 * The new title of the inventory that'll be created.
	 */
	@Setter
	private String inventoryTitle;

	/**
	 * The new size of the inventory that'll be created.
	 */
	private int inventorySize;

	/**
	 * Defines the new size for the inventory.
	 * Can be the total number of slots or the number of horizontal lines in the inventory.
	 *
	 * @param inventorySize the new inventory size.
	 */
	public void setInventorySize(int inventorySize) {
		this.inventorySize = getContainer().normalizeSize(inventorySize);
	}

}
