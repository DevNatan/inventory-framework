package me.devnatan.inventoryframework;

public class InventoryFrameworkException extends RuntimeException {
    public InventoryFrameworkException() {
        super();
    }

    public InventoryFrameworkException(String message) {
        super(message);
    }

    public InventoryFrameworkException(Throwable cause) {
        super(cause);
    }

    public InventoryFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
