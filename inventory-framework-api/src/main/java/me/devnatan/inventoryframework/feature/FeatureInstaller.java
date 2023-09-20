package me.devnatan.inventoryframework.feature;

import java.util.Collection;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * A feature installer stores information about installed features, installs features and ensures
 * that they are not initialized multiple times.
 *
 * @param <P> The platform view framework.
 */
@ApiStatus.NonExtendable
public interface FeatureInstaller<P> {

    /**
     * The platform of this installer.
     *
     * @return The platform of this installer.
     */
    @NotNull
    P getPlatform();

    /**
     * All the features that have already been installed through this installer.
     *
     * @return All installed features.
     */
    Collection<Feature<?, ?, P>> getInstalledFeatures();

    /**
     * Installs a feature.
     *
     * @param feature   The feature to be installed.
     * @param configure The feature configuration.
     * @param <C>       The feature configuration type.
     * @param <R>       The feature value instance type.
     * @return An instance of the installed feature.
     */
    @NotNull
    <C, R> R install(@NotNull Feature<C, R, P> feature, @NotNull UnaryOperator<C> configure);

    /**
     * Uninstalls a feature.
     *
     * @param feature The feature to be uninstalled.
     */
    void uninstall(@NotNull Feature<?, ?, P> feature);
}
