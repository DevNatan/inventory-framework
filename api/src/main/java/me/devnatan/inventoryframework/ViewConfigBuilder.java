package me.devnatan.inventoryframework;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.internal.LayoutPattern;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Getter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class ViewConfigBuilder {

	private String title = "";
	private int size = 0;
	private ViewType type = null;
	private List<ViewConfig.Option<?>> options = new ArrayList<>();
	private String[] layout = null;
	private List<LayoutPattern> patterns = new ArrayList<>();
	private List<ViewConfig.Modifier> modifiers = new ArrayList<>();

	/**
	 * Inherits all configuration from another {@link ViewConfigBuilder} value.
	 * <p>
	 * Note that the values will be merged and not replaced, however, the values of the setting to
	 * be inherited take precedence over those of that setting.
	 *
	 * @param other The configuration that will be inherited.
	 * @return This config.
	 */
	public ViewConfigBuilder inheritFrom(@NotNull ViewConfigBuilder other) {
		throw new UnsupportedOperationException("Inheritance is not yet supported");
	}

	/**
	 * Defines the type of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param type The container type.
	 * @return This config.
	 */
	public ViewConfigBuilder type(ViewType type) {
		this.type = type;
		return this;
	}

	/**
	 * Defines the title of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param title The container title.
	 * @return This config.
	 */
	public ViewConfigBuilder title(String title) {
		this.title = title;
		return this;
	}

	/**
	 * Defines the size of the container.
	 * <p>
	 * If applied in view scope, it will be the default value for all contexts originated from it.
	 *
	 * @param size The container size.
	 * @return This config.
	 */
	public ViewConfigBuilder size(int size) {
		this.size = size;
		return this;
	}

	/**
	 * Add a modifier to this setting.
	 *
	 * @param modifier The modifier.
	 * @return This config.
	 */
	public ViewConfigBuilder with(@NotNull ViewConfig.Modifier modifier) {
		this.modifiers.add(modifier);
		return this;
	}

	public ViewConfigBuilder layout(String... layout) {
		this.layout = layout;
		return this;
	}

	public ViewConfigBuilder layout(char character, @NotNull Consumer<IFItem> handler) {
		throw new UnsupportedOperationException();
	}

	public ViewConfigBuilder layout(char character, @NotNull BiConsumer<Integer, IFItem> handler) {
		throw new UnsupportedOperationException();
	}

	public ViewConfigBuilder option(ViewConfig.Option<?> option) {
		this.options.add(option);
		return this;
	}

	public ViewConfigBuilder options(ViewConfig.Option<?>... options) {
		this.options.addAll(Arrays.asList(options));
		return this;
	}

	public ViewConfigBuilder cancelOnClick() {
		throw new UnsupportedOperationException();
	}

	public ViewConfigBuilder cancelOnPickup() {
		throw new UnsupportedOperationException();
	}

	public ViewConfigBuilder cancelOnDrop() {
		throw new UnsupportedOperationException();
	}

	public ViewConfigBuilder cancelOnDrag() {
		throw new UnsupportedOperationException();
	}
}

