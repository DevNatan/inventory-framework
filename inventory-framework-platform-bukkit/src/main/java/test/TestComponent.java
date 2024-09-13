package test;

import me.devnatan.inventoryframework.component.BukkitComponentBuilder;
import me.devnatan.inventoryframework.component.BukkitComponentHandle;
import me.devnatan.inventoryframework.context.ComponentRenderContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class TestComponent extends BukkitComponentHandle<TestComponent.Builder> {

    @Override
    public TestComponent.Builder builder() {
        return new Builder();
    }

    @Override
    protected void rendered(ComponentRenderContext context) {
        context.getContainer().renderItem(9, new ItemStack(Material.ARROW));
    }

    public static class Builder extends BukkitComponentBuilder<Builder> {

        private Builder() {}

        public Builder something() {
            return this;
        }
    }
}
