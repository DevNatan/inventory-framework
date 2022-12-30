package me.devnatan.inventoryframework;

import me.devnatan.inventoryframework.internal.InitOnly;
import me.saiintbrisson.minecraft.ViewType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@InitOnly
@ApiStatus.Experimental
public interface ViewConfig {

	@ApiStatus.Experimental
	interface Option<T> {

		@NotNull
		String name();

		@Nullable
		T defaultValue();

	}

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

	ViewConfig options(ViewConfig.Option<?>... options);

	ViewConfig cancelOnClick();

	ViewConfig cancelOnPickup();

	ViewConfig cancelOnDrop();

	ViewConfig cancelOnDrag();
}

