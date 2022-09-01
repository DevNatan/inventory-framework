package me.saiintbrisson.minecraft.eventbus;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.saiintbrisson.minecraft.PlatformViewFrame;
import me.saiintbrisson.minecraft.feature.Feature;
import org.jetbrains.annotations.NotNull;

import java.util.function.UnaryOperator;

/**
 * EventBus is a pub/sub feature for IF that simplifies communication between contexts and views.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EventBusFeature implements Feature<Void, Void> {

	@SuppressWarnings("unused")
	public static final Feature<Void, Void> EventBus = new EventBusFeature();

	private static final String MODIFIER = "eventbus";

	@Override
	public @NotNull Void install(
		@NotNull PlatformViewFrame<?, ?, ?> platform,
		@NotNull UnaryOperator<Void> configure
	) {
		platform.getFactory().registerModifier(MODIFIER, view -> {
			// TODO
		});
		return null;
	}

	@Override
	public void uninstall(@NotNull PlatformViewFrame<?, ?, ?> platform) {
		platform.getFactory().unregisterModifier(MODIFIER);
	}

}
