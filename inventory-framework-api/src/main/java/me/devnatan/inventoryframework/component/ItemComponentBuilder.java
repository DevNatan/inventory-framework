package me.devnatan.inventoryframework.component;

import org.jetbrains.annotations.ApiStatus;

public interface ItemComponentBuilder extends ComponentBuilder {

    @ApiStatus.Internal
    void setPosition(int position);

    @ApiStatus.Internal
    ItemComponentBuilder withPosition(int position);

    @ApiStatus.Internal
    ItemComponentBuilder withPlatformItem(Object item);

    @ApiStatus.Internal
    boolean isContainedWithin(int position);
}
