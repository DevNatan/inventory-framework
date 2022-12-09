package me.devnatan.inventoryframework;

import me.saiintbrisson.minecraft.View;
import me.saiintbrisson.minecraft.state.IntState;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class TestView extends View {

    private final IntState counter = state(0);

    public TestView() {
        availableSlot()
                .onRender((render) -> new ItemStack(Material.GOLD_INGOT, counter.get(render)))
                .onClick(counter::decrement);
    }
}
