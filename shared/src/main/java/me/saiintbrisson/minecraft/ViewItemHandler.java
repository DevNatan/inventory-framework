package me.saiintbrisson.minecraft;

import me.devnatan.inventoryframework.IFSlotContext;

/**
 * @deprecated Will be removed soon, available due to backward compatibility. Use {@link
 *     java.util.function.Consumer} instead.
 */
@Deprecated
@FunctionalInterface
public interface ViewItemHandler {

    void handle(IFSlotContext context);
}
