package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.context.ComponentClearContext;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import me.devnatan.inventoryframework.context.ComponentUpdateContext;
import me.devnatan.inventoryframework.context.Context;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.inventory.ItemStack;

public class BukkitComponent extends AbstractBukkitComponent<BukkitComponentBuilder> {

    @SuppressWarnings("unused") // Public API
    protected BukkitComponent() {
        super();
    }

    BukkitComponent(ItemStack item) {
        super(item);
    }

    @Override
    protected void onSetup(Context root, BukkitComponentBuilder config) {}

    @Override
    protected void onRender(ComponentRenderContext render) {}

    @Override
    protected void onUpdate(ComponentUpdateContext update) {}

    @Override
    protected void onClick(SlotClickContext click) {}

    @Override
    protected void onClear(ComponentClearContext clear) {}

    @Override
    public BukkitComponentBuilder builder() {
        return new BukkitComponentBuilder();
    }
}
