package me.devnatan.inventoryframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

@Data
@AllArgsConstructor
@VisibleForTesting
@ApiStatus.NonExtendable
public class ViewConfig {

    public static final ViewConfig.Option<Boolean> CancelOnClick = createOption("cancel-on-click", true);
    public static final ViewConfig.Option<Boolean> CANCEL_ON_PICKUP = createOption("cancel-on-pickup", true);
    public static final ViewConfig.Option<Boolean> CANCEL_ON_DROP = createOption("cancel-on-drop", true);

    private final Object title;
    private final int size;
    private final ViewType type;
    private final Map<Option<?>, Object> options;
    private final String[] layout;
    private final Set<Modifier> modifiers;

    @VisibleForTesting
    @SuppressWarnings("unchecked")
    public <T> T getOptionValue(@NotNull Option<T> option) {
        for (final Map.Entry<Option<?>, Object> entry : getOptions().entrySet()) {
            final Option<?> other = entry.getKey();
            if (other.name().equals(option.name())) return (T) entry.getValue();
        }

        throw new IllegalArgumentException("Unknown option: " + option);
    }

    @VisibleForTesting
    public <T> boolean isOptionSet(@NotNull Option<T> option) {
        for (final Map.Entry<Option<?>, Object> entry : getOptions().entrySet()) {
            final Option<?> other = entry.getKey();
            final Object definedValue = entry.getValue();
            if (other.name().equals(option.name()) && Objects.equals(definedValue, other.defaultValue())) return true;
        }
        return false;
    }

    @VisibleForTesting
    public <T> boolean isOptionSet(@NotNull Option<T> option, T value) {
        for (final Map.Entry<Option<?>, Object> entry : getOptions().entrySet()) {
            final Option<?> other = entry.getKey();
            final Object definedValue = entry.getValue();
            if (other.name().equals(option.name()) && Objects.equals(definedValue, value)) return true;
        }
        return false;
    }

    public static <T> Option<T> createOption(@NotNull String name, @NotNull T defaultValue) {
        return new OptionImpl<>(name, defaultValue);
    }

    /**
     * Merges other config into this configuration.
     *
     * @param other The configuration to be merged.
     * @return A ViewConfig with both {@code this} and the other configuration.
     */
    @ApiStatus.Internal
    @VisibleForTesting
    public ViewConfig merge(ViewConfig other) {
        if (other == null) return this;

        // TODO merge "options" and "modifiers" from both, distinctly
        return new ViewConfig(
                merge(other, ViewConfig::getTitle, Objects::nonNull),
                merge(other, ViewConfig::getSize, value -> value != 0),
                merge(other, ViewConfig::getType),
                merge(other, ViewConfig::getOptions, value -> value != null && !value.isEmpty()),
                merge(other, ViewConfig::getLayout),
                merge(other, ViewConfig::getModifiers, value -> value != null && !value.isEmpty()));
    }

    private <T> T merge(ViewConfig other, Function<ViewConfig, T> retriever) {
        return merge(other, retriever, Objects::nonNull);
    }

    private <T> T merge(ViewConfig other, Function<ViewConfig, T> retriever, Function<T, Boolean> mergeCondition) {
        T value = retriever.apply(other);
        if (!mergeCondition.apply(value)) return retriever.apply(this);
        return value;
    }

    @FunctionalInterface
    public interface Modifier {

        /**
         * Applies this modifier to a given configuration.
         *
         * @param config The target configuration.
         */
        void apply(@NotNull ViewConfigBuilder config);
    }

    public interface Option<T> {

        @NotNull
        String name();

        @Nullable
        T defaultValue();
    }

    @Data
    @Accessors(fluent = true)
    private static final class OptionImpl<T> implements Option<T> {
        private static final List<String> registeredNames = new ArrayList<>();

        private final String name;
        private final T defaultValue;

        OptionImpl(@NotNull String name, T defaultValue) {
            if (registeredNames.contains(name))
                throw new IllegalStateException(String.format("Option %s already registered", name));

            this.name = name;
            this.defaultValue = defaultValue;
        }
    }
}
