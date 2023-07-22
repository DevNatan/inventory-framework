package me.devnatan.inventoryframework.feature;

import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public final class FeatureDescriptor {

    private final String key, name, minimumVersion;

    /**
     * Creates a new feature descriptor.
     *
     * @param key            The feature key identifier.
     * @param name           The feature name.
     * @param minimumVersion The minimum version of the feature.
     */
    FeatureDescriptor(@NotNull String key, @NotNull String name, @NotNull String minimumVersion) {
        this.key = key;
        this.name = name;
        this.minimumVersion = minimumVersion;
    }

    /**
     * The key of the feature.
     *
     * @return The unique feature key.
     */
    public String getKey() {
        return key;
    }

    /**
     * The name of the feature.
     *
     * @return The name of the feature.
     */
    @NotNull
    public String getName() {
        return name;
    }

    /**
     * The minimum required Inventory Framework version that the feature supports.
     *
     * @return The minimum IF required version.
     */
    @NotNull
    public String getMinimumVersion() {
        return minimumVersion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureDescriptor that = (FeatureDescriptor) o;
        return Objects.equals(getKey(), that.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    public String toString() {
        return "FeatureDescriptor{" + "key='"
                + key + '\'' + ", name='"
                + name + '\'' + ", minimumVersion='"
                + minimumVersion + '\'' + '}';
    }
}
