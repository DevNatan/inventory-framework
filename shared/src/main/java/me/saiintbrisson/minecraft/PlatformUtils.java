package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

/** Utility class to define which ViewComponentFactory will be used for the current platform. */
public class PlatformUtils {

    private static ViewComponentFactory factory;

    @NotNull
    public static ViewComponentFactory getFactory() {
        if (factory != null) {
            checkIfPlatformWorksInCurrentPlatform();
            return factory;
        }

        try {
            factory = fallbackFactory();
        } catch (final Exception e) {
            throw new IllegalStateException("Failed to use fallback ViewComponentFactory on classpath.", e);
        }

        checkIfPlatformWorksInCurrentPlatform();
        return factory;
    }

    private static void checkIfPlatformWorksInCurrentPlatform() {
        if (factory.worksInCurrentPlatform()) return;

        throw new IllegalStateException("We found a ViewComponentFactory on the classpath but it is not usable on this "
                + "platform, make sure you have in your classpath an implementation of this class "
                + "that is functional on the current platform.");
    }

	@ApiStatus.Internal
    public static void setFactory(@NotNull ViewComponentFactory factory) {
        final ViewComponentFactory curr = PlatformUtils.factory;
        if (curr != null) return;

        PlatformUtils.factory = factory;
    }

    @TestOnly
    static void removeFactory() {
        PlatformUtils.factory = null;
    }

    private static ViewComponentFactory fallbackFactory()
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        final Class<?> clazz = Class.forName("me.saiintbrisson.minecraft.BukkitViewComponentFactory");
        return (ViewComponentFactory) clazz.newInstance();
    }
}
