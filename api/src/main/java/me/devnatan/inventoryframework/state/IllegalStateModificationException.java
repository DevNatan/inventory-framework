package me.devnatan.inventoryframework.state;

public class IllegalStateModificationException extends StateException {
    public IllegalStateModificationException(String message) {
        super(message);
    }
}
