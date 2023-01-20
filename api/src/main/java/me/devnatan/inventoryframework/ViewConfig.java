package me.devnatan.inventoryframework;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Data
public final class ViewConfig {

	private final String title;
	private final int size;
	private final ViewType type;
	private final List<ViewConfigBuilder> options;
	private final String[] layout;
	private final List<Modifier> modifiers;

	interface Option<T> {

		@NotNull
		String name();

		@Nullable
		T defaultValue();

	}

	@FunctionalInterface
	interface Modifier {

		/**
		 * Applies this modifier to a given configuration.
		 *
		 * @param config The target configuration.
		 */
		void apply(@NotNull ViewConfigBuilder config);
	}

}
