package me.saiintbrisson.minecraft;

import com.google.common.base.Preconditions;
import org.bukkit.entity.Player;

public final class OpenViewContext extends ViewContext {

    private String inventoryTitle;

    public OpenViewContext(View view, Player player) {
        super(view, player, null);
    }

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

}
