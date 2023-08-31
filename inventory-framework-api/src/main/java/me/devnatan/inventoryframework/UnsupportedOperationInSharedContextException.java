package me.devnatan.inventoryframework;

/**
 * Thrown when an operation is called in a shared context, but it's not supported for some reason.
 *
 * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/Shared-Contexts">Shared Contexts on Wiki</a>
 */
public final class UnsupportedOperationInSharedContextException extends InventoryFrameworkException {

    public UnsupportedOperationInSharedContextException() {
        super("This operation is not supported in shared contexts.");
    }

    public UnsupportedOperationInSharedContextException(String replacement) {
        super(String.format("This operation is not supported in shared contexts. Use #%s instead.", replacement));
    }
}
