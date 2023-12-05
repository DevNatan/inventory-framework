package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.jetbrains.annotations.ApiStatus;

public interface ComponentBuilder {

    /**
     * Builds a component from this component builder.
     * <p>
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     *
     * @return A new component instance built from this component builder.
     */
    @ApiStatus.Internal
    Component build(VirtualView root);
}
