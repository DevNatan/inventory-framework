package me.devnatan.inventoryframework;

import static me.devnatan.inventoryframework.runtime.thirdparty.ReflectionUtils.getNMSClass;

import java.lang.reflect.Constructor;
import me.devnatan.inventoryframework.runtime.thirdparty.ReflectionUtils;

class Protocol {

    private static Constructor<?> ANVIL_CONSTRUCTOR;
    private static Class<?> BLOCK_POSITION;

    static {
        try {
            BLOCK_POSITION = getNMSClass("BlockPosition");
            ANVIL_CONSTRUCTOR = getNMSClass("ContainerAnvil")
                    .getConstructor(
                            getNMSClass("PlayerInventory"),
                            getNMSClass("World"),
                            BLOCK_POSITION,
                            getNMSClass("EntityHuman"));
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Unsupported version for Anvil Input feature: " + ReflectionUtils.getVersionInformation(),
                    exception);
        }
    }
}
