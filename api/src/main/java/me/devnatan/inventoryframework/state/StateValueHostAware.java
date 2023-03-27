package me.devnatan.inventoryframework.state;

import org.jetbrains.annotations.NotNull;

public interface StateValueHostAware extends StateValueHost {

    @NotNull
    StateValueHost getStateValueHost();
}
