package me.saiintbrisson.minecraft;

import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveInOutFeature implements Feature<Void, Void> {

    public static final Feature<Void, Void> MoveInOut = new MoveInOutFeature();

    private static final String MODIFIER = "move-in-out";

    @Override
    public @NotNull Void install(
            @NotNull PlatformViewFrame<?, ?, ?> platform, @NotNull UnaryOperator<Void> configure) {
        platform.getFactory()
                .registerModifier(
                        MODIFIER,
                        view -> {
                            view.getPipeline()
                                    .intercept(AbstractView.CLICK, new BukkitMoveOutInterceptor());
                        });
        return null;
    }

    @Override
    public void uninstall(@NotNull PlatformViewFrame<?, ?, ?> platform) {
        platform.getFactory().unregisterModifier(MODIFIER);
    }
}
