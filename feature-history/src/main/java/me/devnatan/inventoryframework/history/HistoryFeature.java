package me.devnatan.inventoryframework.history;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.IFViewFrame;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.feature.FeatureInstaller;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.PipelineInterceptor;
import me.devnatan.inventoryframework.pipeline.PipelinePhase;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;

/**
 * History feature allows the developer to save the last context the player was in so that it can
 * be resumed later.
 * <p>
 * A context resumed from the history must be in the exact same state as when it was dispatched,
 * basically a snapshot of it is saved and reused.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HistoryFeature implements Feature<Void, HistoryFeature, FeatureInstaller<?>> {

    public static final Feature<Void, HistoryFeature, FeatureInstaller<?>> History = new HistoryFeature();

    /**
     * Unordered phase executed when a context from history is resumed.
     */
    private static final PipelinePhase HistoryResume = new PipelinePhase("history-resume");

    /**
     * Map containing the history of contexts, the key is the id of the originating context and
     * the value is the context that precedes it.
     */
    private final Map<UUID, IFContext> history = new HashMap<>();

    @Override
    public HistoryFeature install(FeatureInstaller<?> framework, UnaryOperator<Void> configure) {
        if (!(framework instanceof IFViewFrame))
            throw new IllegalStateException("History feature only supports ViewFrame as installer.");

        ((IFViewFrame<?>) framework).getRegisteredViews().values().forEach(this::setup);
        return this;
    }

    @Override
    public void uninstall(FeatureInstaller<?> framework) {
        if (!(framework instanceof IFViewFrame)) return;

        for (final RootView view :
                ((IFViewFrame<?>) framework).getRegisteredViews().values()) {
            view.getPipeline().removePhase(HistoryResume);
        }
    }

    private void setup(RootView view) {
        final Pipeline<? super VirtualView> pipeline = view.getPipeline();

        pipeline.intercept(StandardPipelinePhases.INIT, (PipelineInterceptor<RootView>) (context, subject) -> {
            pipeline.addPhase(HistoryResume);
        });

        pipeline.intercept(StandardPipelinePhases.OPEN, (PipelineInterceptor<IFContext>) (context, subject) -> {
            // TODO
        });

        pipeline.intercept(StandardPipelinePhases.CLOSE, (PipelineInterceptor<IFCloseContext>) (context, subject) -> {
            // TODO do it, and, close reason must be a context switch (view-to-view)
        });
    }

    private void resume(IFContext initiator, IFContext target) {
        throw new UnsupportedOperationException("TODO");
    }
}
