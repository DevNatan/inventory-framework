package me.devnatan.inventoryframework.jdk;

/**
 * Represents a function that accepts and int-valued iteration index as first argument
 * and a int-valued slot position as second argument and produces a result.
 *
 * @param <R> The type of the result of the function.
 */
@FunctionalInterface
public interface IndexSlotFunction<R> {

    /**
     * Applies this function to the given argument.
     *
     * @param index The iteration index.
     * @param slot  The slot position.
     * @return The function result.
     */
    R apply(int index, int slot);
}
