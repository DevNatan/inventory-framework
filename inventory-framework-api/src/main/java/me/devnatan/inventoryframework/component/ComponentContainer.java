package me.devnatan.inventoryframework.component;

import java.util.List;
import org.jetbrains.annotations.ApiStatus;

public interface ComponentContainer {

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    List<Component> getComponents();
}
