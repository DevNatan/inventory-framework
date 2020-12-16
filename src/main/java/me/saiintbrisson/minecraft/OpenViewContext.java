package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

import static me.saiintbrisson.minecraft.View.INVENTORY_ROW_SIZE;

/**
 * This context happens before the inventory is opened, it is used for cancellation by previously defined data,
 * for any reason and can be used to change the title and size of the inventory before the rendering intent.
 */
public final class OpenViewContext extends ViewContext {

    private String inventoryTitle;
    private int inventorySize;

    public OpenViewContext(View view, Player player) {
        super(view, player, null);
        inventorySize = view.getLastSlot() + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViewItem slot(int slot) {
        throw new UnsupportedOperationException("Cannot define items before the inventory is opened.");
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
     * @param inventorySize the new inventory size.
     */
    public void setInventorySize(int inventorySize) {
        // is less than nine, probably the person thought it was to put the row so we convert at once
        this.inventorySize = inventorySize < 9 ? inventorySize * INVENTORY_ROW_SIZE : inventorySize;
    }

}
