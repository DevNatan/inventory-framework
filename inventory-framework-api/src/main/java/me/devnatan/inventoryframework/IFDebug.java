package me.devnatan.inventoryframework;

import java.util.function.Supplier;
import org.intellij.lang.annotations.PrintFormat;
import org.jetbrains.annotations.ApiStatus;

/**
 * <b><i> This is an internal inventory-framework API that should not be used from outside of
 * this library. No compatibility guarantees are provided. </i></b>
 */
@ApiStatus.Internal
public final class IFDebug {

    private static final String PREFIX = "[IF]";
    private static final String SYSTEM_PROPERTY = "me.devnatan.inventoryframework.debug";

    private static Boolean DEBUG_ENABLED = null;

    private IFDebug() {}

    /**
     * Returns if debug is enabled.
     *
     * @return If debug is enabled.
     */
    public static boolean isDebugEnabled() {
        if (DEBUG_ENABLED == null) DEBUG_ENABLED = Boolean.parseBoolean(System.getProperty(SYSTEM_PROPERTY, "false"));

        return DEBUG_ENABLED;
    }

    /**
     * Enables InventoryFramework debug.
     *
     * @param enabled If debug should be enabled.
     */
    public static void setEnabled(boolean enabled) {
        System.setProperty(SYSTEM_PROPERTY, String.valueOf(enabled));
    }

    /**
     * Prints a message if debug is enabled.
     *
     * @param message Message to print
     * @param args Arguments to apply to the message
     */
    public static void debug(Supplier<String> message, Object... args) {
        if (!isDebugEnabled()) return;
        System.out.println(PREFIX + " " + String.format(message.get(), args));
    }

    /**
     * Prints a message if debug is enabled.
     *
     * @param message Message to print
     * @param args Arguments to apply to the message
     */
    public static void debug(@PrintFormat String message, Object... args) {
        if (!isDebugEnabled()) return;
        System.out.println(PREFIX + " " + String.format(message, args));
    }
}
