package me.saiintbrisson.minecraft;

import java.util.ArrayList;
import me.devnatan.inventoryframework.state.PaginationState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

class MyView extends View {

    private final PaginationState<String> paginationState = paginationState(ArrayList::new, this::onItemRender);

    private void onItemRender(ViewItem item, String value) {
        item.withItem(new ItemStack(Material.GOLD_INGOT));
    }
}

@SuppressWarnings("unused")
public final class InventoryFramework extends JavaPlugin {}
