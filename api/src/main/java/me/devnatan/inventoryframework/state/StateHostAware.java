package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface StateHostAware extends StateHost {

    @NotNull
    StateHost getStateHost();
}
