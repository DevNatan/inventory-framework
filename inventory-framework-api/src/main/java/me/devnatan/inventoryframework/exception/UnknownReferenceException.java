package me.devnatan.inventoryframework.exception;

import me.devnatan.inventoryframework.InventoryFrameworkException;

public final class UnknownReferenceException extends InventoryFrameworkException {
    public UnknownReferenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
