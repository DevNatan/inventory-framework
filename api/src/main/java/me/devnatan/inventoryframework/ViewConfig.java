package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.internal.InitOnly;
import me.saiintbrisson.minecraft.ViewType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.Supplier;

@InitOnly
@ApiStatus.Experimental
public interface ViewConfig {

	@FunctionalInterface
	@ApiStatus.Experimental
	interface Modifier {

		/**
		 * Applies this modifier to a given configuration.
		 *
		 * @param config The target configuration.
		 */
		void apply(@NotNull ViewConfig config);
	}

	/**
	 * Creates a new {@link ViewConfig}.
	 *
	 * @return A new ViewConfig instance.
	 */
	@NotNull
	static ViewConfig create() {
		return new Impl();
	}

	/**
	 * Creates a new {@link ViewConfig}.
	 *
	 * @return A new ViewConfig instance.
	 */
	@NotNull
	static ViewConfig create(String title) {
		return new Impl().title(title);
	}

	/**
	 * Creates a new {@link ViewConfig}.
	 *
	 * @return A new ViewConfig instance.
	 */
	@NotNull
	static ViewConfig create(int size, String title) {
		return new Impl().size(size).title(title);
	}

	/**
	 * All modifiers applied to this configuration.
	 *
	 * @return An unmodifiable list of all applied modifiers.
	 */
	@NotNull
	@Unmodifiable
	List<Modifier> getAppliedModifiers();

	/**
	 * Inherits all configuration from another {@link ViewConfig} value.
	 * <p>
	 * Note that the values will be merged and not replaced, however, the values of the setting to
	 * be inherited take precedence over those of that setting.
	 *
	 * @param other The configuration that will be inherited.
	 * @return This config.
	 */
	ViewConfig inheritFrom(@NotNull ViewConfig other);

	/**
	 * Defines the type of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param type The container type.
	 * @return This config.
	 */
	ViewConfig type(ViewType type);

	/**
	 * Defines the title of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param title The container title.
	 * @return This config.
	 */
	ViewConfig title(String title);

	/**
	 * Defines the size of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param size The container size.
	 * @return This config.
	 */
	ViewConfig size(int size);

	/**
	 * Add a modifier to this setting.
	 *
	 * @param modifier The modifier.
	 * @return This config.
	 */
	ViewConfig with(@NotNull Modifier modifier);

	ViewConfig layout(String... layout);

	ViewConfig layout(char character, @NotNull Consumer<ViewItem> handler);

	ViewConfig layout(char character, @NotNull BiConsumer<Integer, ViewItem> handler);

	ViewConfig layout(char character, @NotNull Supplier<Object> factory);

	ViewConfig flags(int flags);

	ViewConfig flags(int flag, int... others);

	ViewConfig cancelOnClick();
}

