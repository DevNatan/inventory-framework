package me.devnatan.inventoryframework.exception;

public class SlotFillExceededException extends RuntimeException {

    public SlotFillExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
