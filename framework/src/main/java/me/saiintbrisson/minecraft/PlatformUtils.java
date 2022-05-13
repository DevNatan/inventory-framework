package me.saiintbrisson.minecraft;

import org.jetbrains.annotations.NotNull;

/**
 * Utility class to define which ViewComponentFactory will be used for the current platform.
 */
class PlatformUtils {

	private static ViewComponentFactory factory;

	@NotNull
	static ViewComponentFactory getFactory() {
		if (factory != null)
			return factory;

		try {
			factory = fallbackFactory();
		} catch (final Exception e) {
			throw new IllegalStateException("Failed to use fallback ViewComponentFactory on classpath.", e);
		}

		if (!factory.worksInCurrentPlatform())
			throw new IllegalStateException(
				"We found a ViewComponentFactory on the classpath but it is not usable on this " +
					"platform, make sure you have in your classpath an implementation of this class " +
					"that is functional on the current platform."
			);

		return factory;
	}

	static void setFactory(@NotNull ViewComponentFactory factory) {
		final ViewComponentFactory curr = PlatformUtils.factory;
		if (curr != null)
			throw new IllegalStateException(
				"It is not allowed to define the View Component Factory more than once on the same platform."
			);

		PlatformUtils.factory = factory;
	}

	private static ViewComponentFactory fallbackFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		final Class<?> clazz = Class.forName("me.saiintbrisson.BukkitViewComponentFactory");
		return (ViewComponentFactory) clazz.newInstance();
	}

}