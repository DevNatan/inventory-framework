package me.devnatan.inventoryframework.exception;

import me.devnatan.inventoryframework.InventoryFrameworkException;
import me.devnatan.inventoryframework.context.IFContext;

/**
 * Thrown when an error related to a {@link IFContext#getContainer() context container} occurs.
 */
public class ContainerException extends InventoryFrameworkException {

    public ContainerException(String message, Throwable cause) {
        super(message, cause);
    }
}
