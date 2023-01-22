package me.devnatan.inventoryframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Data
public final class ViewConfig {

    private final String title;
    private final int size;
    private final ViewType type;
    private final Map<Option<?>, Object> options;
    private final String[] layout;
    private final List<Modifier> modifiers;

    public static <T> Option<T> createOption(@NotNull String name, @NotNull T defaultValue) {
        return new OptionImpl<>(name, defaultValue);
    }

    public <T> boolean isOptionSet(@NotNull Option<T> option) {
        for (final Map.Entry<Option<?>, Object> entry : options.entrySet()) {
            final Option<?> other = entry.getKey();
            final Object definedValue = entry.getValue();
            if (other.name().equals(option.name()) && Objects.equals(definedValue, other.defaultValue())) return true;
        }
        return false;
    }

    public <T> boolean isOptionSet(@NotNull Option<T> option, T value) {
        for (final Map.Entry<Option<?>, Object> entry : options.entrySet()) {
            final Option<?> other = entry.getKey();
            final Object definedValue = entry.getValue();
            if (other.name().equals(option.name()) && Objects.equals(definedValue, value)) return true;
        }
        return false;
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

    interface Option<T> {

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
                throw new IllegalStateException(String.format("Option %s already exists", name));

            this.name = name;
            this.defaultValue = defaultValue;
        }
    }
}
