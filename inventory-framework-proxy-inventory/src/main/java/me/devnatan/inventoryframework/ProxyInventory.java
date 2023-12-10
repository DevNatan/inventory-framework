package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class ProxyInventory implements ViewConfig.Modifier {

    private final ViewContainer proxiedContainer;

    private ProxyInventory(ViewContainer proxiedContainer) {
        this.proxiedContainer = proxiedContainer;
    }

    @Override
    public void apply(@NotNull ViewConfigBuilder config, @NotNull IFContext context) {
        final ViewType containerType = context.getConfig().getType();
        if (!containerType.isExtendable())
            throw new IllegalStateException(String.format(
                    "Proxy feature can only be applied in extendable inventory types. %s is not extendable.",
                    containerType));

        final IFOpenContext openContext = (IFOpenContext) context;
        openContext.setContainer(new ProxyContainer(openContext.getContainer(), proxiedContainer));
    }

    /**
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     */
    @ApiStatus.Experimental
    public static ProxyInventory createProxy(Inventory inventory) {
        return new ProxyInventory(new BukkitViewContainer(inventory, ViewType.PLAYER, true, false));
    }
}
