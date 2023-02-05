package me.devnatan.inventoryframework.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IsTypeOf {

	public static boolean isTypeOf(@NotNull Class<?> superCls, @NotNull Class<?> cls) {
		return superCls.isAssignableFrom(cls);
	}

}
