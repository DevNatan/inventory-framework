package me.devnatan.inventoryframework.feature;

import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.ViewFrame;
import org.jetbrains.annotations.NotNull;

/**
 * A feature is a module that is installed in the runtime and acts independently of the rest of the
 * platform.
 *
 * <p>Features are unknown internally and can only be accessed from themselves or from a reference
 * created by them, as a recommendation, a feature must be immutable and preferably inaccessible
 * outside the package of the feature itself, only the reference of that Feature used to install and
 * obtain the its instance value must be externally accessible.
 *
 * <p>The recommended style of building a feature is to use a helper class as an instance value,
 * which will be taken when the Feature is referenced, and the class of the feature itself is
 * private while its key is public for later installation.
 *
 * <h3>Configuration</h3>
 * <p>
 * Since features are immutable at runtime, there needs to be a way to pre-define their initial
 * values in case it is necessary for them to act together with these values, defined by the user,
 * for that there is the FeatureConfig, which is the first parameter of the Feature interface, which
 * must be a __configuration builder__ and not the configuration itself.
 *
 * <pre><code>
 * {@literal @}Builder(builderClassName = "Builder")
 * {@literal @}Data
 * {@literal @}RequiredArgsConstructor(access = AccessLevel.PRIVATE)
 *  final class SampleFeatureConfig {}
 * </code></pre>
 *
 * <h3>Preventing instance leak</h3>
 * <p>
 * It is important to note that features are singleton instances and can only be modified (by
 * themselves) once in their entire lifecycle, which is during their installation. The
 * FeatureInstaller must ensure that this is applied and the Feature instance can also add a second
 * check to ensure that there are not multiple references to a single feature.
 *
 * @param <C> The feature configuration type.
 * @param <R> The feature return value type.
 */
public interface Feature<C, R> {

    /**
     * Initializes and installs everything that must be installed within this feature using the
     * platform and configuration factory as providers.
     *
     * @param framework The feature installer platform framework.
     * @param configure The feature configuration factory.
     * @return The initialized feature instance.
     */
    @NotNull
    R install(ViewFrame framework, UnaryOperator<C> configure);

    /**
     * Uninstalls this feature, used to invalidate resources applied on installation.
     *
     * @param framework The feature uninstaller framework.
     * @see #install(ViewFrame, UnaryOperator)
     */
    void uninstall(ViewFrame framework);
}
