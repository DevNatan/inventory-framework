package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.context.IFContext;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Ref's hold a reference of any element inside a {@link me.devnatan.inventoryframework.context.IFContext}.
 * <p>
 * <b><i> This API is experimental and is not subject to the general compatibility guarantees
 * such API may be changed or may be removed completely in any further release. </i></b>
 *
 * @param <E> Type of the value that this ref is holding.
 */
@ApiStatus.Experimental
public interface Ref<E> {

    /**
     * Returns the value hold by this reference.
     *
     * @throws UnassignedReferenceException If reference wasn't assigned to any element.
     * @return The value that this reference holds.
     */
    @NotNull
    E value(@NotNull IFContext context);
}
