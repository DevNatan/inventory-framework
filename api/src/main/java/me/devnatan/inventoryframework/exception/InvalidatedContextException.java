package me.devnatan.inventoryframework.exception;

public class InvalidatedContextException extends InventoryFrameworkException {
    InvalidatedContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
