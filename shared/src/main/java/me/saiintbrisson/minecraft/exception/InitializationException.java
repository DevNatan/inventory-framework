package me.saiintbrisson.minecraft.exception;

/**
 * Called when a function that changes the nature of the view is called after its initialization.
 *
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Errors#initializationexception">InitializationException on Wiki</a>
 */
public class InitializationException extends InventoryFrameworkException {

    public InitializationException() {
        super(
                "It is not allowed to call this function after the initialization of the view because it changes its nature. "
                        + "You probably called a function that should be in the constructor in a handler (e.g.: onRender or onUpdate)? "
                        + "If this is the case, use `context.yourFunction()` if available on that handler instead of `yourFunction()`. ",
                null);
    }
}
