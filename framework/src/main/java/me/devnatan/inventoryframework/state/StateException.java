package me.devnatan.inventoryframework.state;

import me.devnatan.inventoryframework.exception.InventoryFrameworkException;

public class StateException extends InventoryFrameworkException {
    public StateException() {
        super();
    }

    public StateException(String message, Throwable cause) {
        super(message, cause);
    }
}
