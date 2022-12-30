package me.saiintbrisson.minecraft;

import static me.devnatan.inventoryframework.pipeline.StandardPipelinePhases.CLICK;

import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.feature.Feature;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveInOutFeature implements Feature<Void, Void> {

    public static final Feature<Void, Void> MoveInOut = new MoveInOutFeature();

    private static final String MODIFIER = "move-in-out";

    @Override
    public @NotNull Void install(@NotNull PlatformViewFrame<?, ?, ?> platform, @NotNull UnaryOperator<Void> configure) {
        platform.getFactory().registerModifier(MODIFIER, view -> {
            view.getPipeline().intercept(CLICK, new BukkitMoveOutInterceptor());
        });
        return null;
    }

    @Override
    public void uninstall(@NotNull PlatformViewFrame<?, ?, ?> platform) {
        platform.getFactory().unregisterModifier(MODIFIER);
    }
}
