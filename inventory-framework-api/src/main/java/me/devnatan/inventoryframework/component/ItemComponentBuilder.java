package me.devnatan.inventoryframework.component;

import me.devnatan.inventoryframework.VirtualView;
import org.jetbrains.annotations.ApiStatus;

public interface ItemComponentBuilder extends ComponentBuilder {

    @ApiStatus.Internal
    void setPosition(int position);

    @ApiStatus.Internal
    boolean isContainedWithin(int position);

    @Override
    ItemComponent build(VirtualView root);
}
