package me.devnatan.inventoryframework.config;

import me.saiintbrisson.minecraft.ViewType;
import me.saiintbrisson.minecraft.internal.InitOnly;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
	 */
	@Contract(value = "_ -> this", mutates = "this")
	ViewConfig inheritFrom(@NotNull ViewConfig other);

	/**
	 * Defines the type of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param type The container type.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	ViewConfig type(ViewType type);

	/**
	 * Defines the title of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param title The container title.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	ViewConfig title(String title);

	/**
	 * Defines the size of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param size The container size.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	ViewConfig size(int size);

	/**
	 * Add a modifier to this setting.
	 *
	 * @param modifier The modifier.
	 */
	@Contract(value = "_ -> this", mutates = "this")
	ViewConfig with(@NotNull Modifier modifier);

	/**
	 * Creates a new {@link ViewConfig} implementation.
	 *
	 * @return A new ViewConfig implementation.
	 */
	@NotNull
	static ViewConfig create() {
		return new Impl();
	}

}

/**
 * Default implementation for ViewConfig.
 */
class Impl implements ViewConfig {

	final List<Modifier> modifierList = new LinkedList<>();

	private String containerTitle;
	private ViewType containerType;
	private int containerSize;

	@Override
	public @NotNull @Unmodifiable List<Modifier> getAppliedModifiers() {
		return Collections.unmodifiableList(modifierList);
	}

	@Override
	public ViewConfig inheritFrom(@NotNull ViewConfig other) {
		return this;
	}

	@Override
	public ViewConfig type(ViewType type) {
		containerType = type;
		return this;
	}

	@Override
	public ViewConfig title(String title) {
		containerTitle = title;
		return this;
	}

	@Override
	public ViewConfig size(int size) {
		containerSize = size;
		return this;
	}

	@Override
	public ViewConfig with(@NotNull Modifier modifier) {
		modifierList.add(modifier);
		return this;
	}
}
