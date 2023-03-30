package me.devnatan.inventoryframework.exception;

import me.devnatan.inventoryframework.InventoryFrameworkException;

public final class UnresolvedLayoutException extends InventoryFrameworkException {
    public UnresolvedLayoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
