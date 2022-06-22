package me.saiintbrisson.minecraft;

/**
 * @deprecated Will be removed soon, available due to backward compatibility. Use {@link
 *     java.util.function.Consumer} instead.
 */
@Deprecated
@FunctionalInterface
public interface ViewItemHandler {

    void handle(ViewSlotContext context);
}
