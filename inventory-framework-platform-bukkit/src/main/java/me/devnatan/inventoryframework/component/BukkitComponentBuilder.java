package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.jetbrains.annotations.ApiStatus;

public final class BukkitComponentBuilder extends AbstractBukkitComponentBuilder<BukkitComponentBuilder> {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public BukkitComponentBuilder() {}

    @Override
    public Component buildComponent(VirtualView root) {
        return new BukkitComponent();
    }
}
