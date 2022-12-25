package me.devnatan.inventoryframework.exception;

/**
 * Called when a function that changes the nature of the view is called after its initialization.
 *
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Errors#initializationexception">InitializationException on Wiki</a>
 */
public class InitializationException extends InventoryFrameworkException {

    public InitializationException() {
        super("This function cannot be called after initialization.", null);
    }
}
