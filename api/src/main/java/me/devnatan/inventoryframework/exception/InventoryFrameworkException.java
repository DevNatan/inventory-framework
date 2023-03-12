package me.devnatan.inventoryframework.exception;

public class InventoryFrameworkException extends RuntimeException {
    public InventoryFrameworkException() {
        super();
    }

    public InventoryFrameworkException(String message) {
        super(message);
    }

    public InventoryFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
