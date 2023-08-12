package me.devnatan.inventoryframework.exception;

import me.devnatan.inventoryframework.InventoryFrameworkException;

public class InvalidatedContextException extends InventoryFrameworkException {
    InvalidatedContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
