package me.devnatan.inventoryframework.exception;

/**
 * Thrown when a method explicitly needs to specify that it will directly modify the view's container
 * when executed, that method is overridden by implementations whose direct modification of the
 * container is not allowed, throwing an exception.
 *
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Errors#inventorymodificationexception">InventoryModificationException on Wiki</a>
 */
public class InventoryModificationException extends InventoryFrameworkException {

    public InventoryModificationException(String message) {
        super(message, null);
    }
}
