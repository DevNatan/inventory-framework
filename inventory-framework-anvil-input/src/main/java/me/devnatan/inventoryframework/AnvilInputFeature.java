package me.devnatan.inventoryframework;

import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.pipeline.PipelineContext;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class AnvilInputFeature<F extends IFViewFrame<?, ?>> implements Feature<Void, Void, F> {

    /**
     * Instance of the Anvil Input feature.
     *
     * @see <a href="https://github.com/DevNatan/inventory-framework/wiki/anvil-input">Anvil Input on Wiki</a>
     */
    public static final AnvilInputFeature<?> AnvilInput = new AnvilInputFeature<>();

    private final PipelineInterceptor frameInterceptor = new FrameInterceptor();

    private AnvilInputFeature() {}

    @Override
    public @NotNull String name() {
        return "Anvil Input";
    }

    @Override
    public @NotNull Void install(F framework, UnaryOperator<Void> configure) {
        return null;
    }

    @Override
    public void uninstall(F framework) {
        framework.getPipeline().removeInterceptor(IFViewFrame.FRAME_REGISTERED, frameInterceptor);
    }
}

@SuppressWarnings("rawtypes")
class FrameInterceptor implements PipelineInterceptor<IFViewFrame> {

    @Override
    public void intercept(PipelineContext<IFViewFrame> pipeline, IFViewFrame subject) {}
}
