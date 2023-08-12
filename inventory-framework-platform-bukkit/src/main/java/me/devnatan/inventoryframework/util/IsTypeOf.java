package me.devnatan.inventoryframework.util;

import org.jetbrains.annotations.NotNull;

public final class IsTypeOf {

    private IsTypeOf() {}

    public static boolean isTypeOf(@NotNull Class<?> superCls, @NotNull Class<?> cls) {
        return superCls.isAssignableFrom(cls);
    }
}
