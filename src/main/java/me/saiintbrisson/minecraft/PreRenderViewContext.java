package me.saiintbrisson.minecraft;

import org.bukkit.entity.Player;

public class PreRenderViewContext extends ViewContext {

    private String inventoryTitle;

    public PreRenderViewContext(View view, Player player) {
        super(view, player, null);
    }

    @Override
    public ViewItem slot(int slot) {
        throw new UnsupportedOperationException();
    }

    public String getInventoryTitle() {
        return inventoryTitle;
    }

    public void setInventoryTitle(String inventoryTitle) {
        this.inventoryTitle = inventoryTitle;
    }

}
