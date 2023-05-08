package me.devnatan.inventoryframework.exception;

import me.devnatan.inventoryframework.InventoryFrameworkException;

public final class InvalidSizeException extends InventoryFrameworkException {
    public InvalidSizeException(String message) {
        super(message);
    }
}
