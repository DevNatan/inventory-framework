package me.devnatan.inventoryframework.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.devnatan.inventoryframework.internal.InitOnly;
import me.saiintbrisson.minecraft.ViewType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

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

    ViewConfig flags(int flags);

    ViewConfig flags(int flag, int... others);
}

/**
 * Default implementation for ViewConfig.
 */
@Setter
@Accessors(chain = true, fluent = true)
final class Impl implements ViewConfig {

    final List<Modifier> modifierList = new LinkedList<>();

    private String title;
    private ViewType type;
    private int size;
    private String[] layout;
    private int flags;

    @Override
    public @NotNull @Unmodifiable List<Modifier> getAppliedModifiers() {
        return Collections.unmodifiableList(modifierList);
    }

    @Override
    public ViewConfig inheritFrom(@NotNull ViewConfig other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ViewConfig with(@NotNull Modifier modifier) {
        modifierList.add(modifier);
        return this;
    }

    @Override
    public ViewConfig layout(String... layout) {
        this.layout = layout;
        return this;
    }

    @Override
    public ViewConfig flags(int flags) {
        this.flags = flags;
        return this;
    }

    @Override
    public ViewConfig flags(int flag, int... others) {
        int value = flag;
        for (final int other : others) value = value | other;
        this.flags = value;
        return this;
    }
}
