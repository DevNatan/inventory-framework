package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MoveInOutFeature implements Feature<Void, Void> {

	public static final Feature<Void, Void> MoveInOut = new MoveInOutFeature();

	@Override
	public @NotNull Void install(@NotNull PlatformViewFrame<?, ?, ?> platform, @NotNull UnaryOperator<Void> configure) {
		platform.getFactory().registerModifier(view -> {
//			view.getPipeline().intercept(
//				AbstractView.CLICK,
//				new BukkitMoveInInterceptor()
//			);
			view.getPipeline().intercept(
				AbstractView.CLICK,
				new BukkitMoveOutInterceptor()
			);
		});
		return null;
	}

}
