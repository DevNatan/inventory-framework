package me.devnatan.inventoryframework.jdk;

/**
 * Represents an operation that accepts an iteration index as first argument, an int-valued slot position as second argument
 * and an object-valued input as third argument, and returns no result.
 *
 * @param <T> The type of the object argument to the operation.
 */
@FunctionalInterface
public interface IndexSlotConsumer<T> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param index The iteration index.
     * @param slot  The slot position.
     * @param value The input argument.
     */
    void accept(int index, int slot, T value);
}
