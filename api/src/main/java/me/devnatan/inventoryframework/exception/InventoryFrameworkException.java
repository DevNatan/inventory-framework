package me.devnatan.inventoryframework.exception;

public class InventoryFrameworkException extends RuntimeException {
    protected InventoryFrameworkException() {
        super();
    }

    protected InventoryFrameworkException(String message) {
        super(message);
    }

    protected InventoryFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
