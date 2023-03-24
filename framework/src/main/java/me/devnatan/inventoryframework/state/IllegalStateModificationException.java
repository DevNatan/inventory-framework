package me.devnatan.inventoryframework.state;

public class IllegalStateModificationException extends StateException {
    public IllegalStateModificationException() {
        super();
    }

	public IllegalStateModificationException(String message) {
		super(message);
	}
}
