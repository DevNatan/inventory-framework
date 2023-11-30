package me.devnatan.inventoryframework.examples;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.example.SnakeGameView;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class InventoryFrameworkExamples extends JavaPlugin {

	private static final BiFunction<Player, String[], Map<String, Object>> NullInitialDataFactory = ($, $$) -> null;
	private static final List<SampleView> views = ImmutableList.<SampleView>builder()
		.add(new SampleView(SnakeGameView.class, new SnakeGameView(), NullInitialDataFactory))
		.build();

	private ViewFrame vf;

	@Override
	public void onEnable() {
		vf = ViewFrame.create(this)
			.with(views.stream().map(SampleView::getInstance).toArray(View[]::new))
			.register();

		Objects.requireNonNull(getCommand("ifexample")).setExecutor(this);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		final SampleView target;
		if (args.length == 0) {
			target = views.get(0);
		} else {
			target = views.stream()
				.filter(sampleView -> sampleView.getClazz()
					.getSimpleName()
					.substring(0, sampleView.getClazz().getSimpleName().length() - "View".length())
					.equals(args[0]))
				.findFirst()
				.orElse(null);

			if (target == null) {
				sender.sendMessage("Sample not found");
				return true;
			}
		}

		vf.open(
			target.getClazz(),
			(Player) sender,
			target.getInitialDataFactory().apply((Player) sender, Arrays.copyOfRange(args, 1, args.length - 1))
		);
		return true;
	}
}

class SampleView {

	private final Class<? extends View> clazz;
	private final View instance;
	private final BiFunction<Player, String[], Map<String, Object>> initialDataFactory;

	public SampleView(Class<? extends View> clazz, View instance, BiFunction<Player, String[], Map<String, Object>> initialDataFactory) {
		this.clazz = clazz;
		this.instance = instance;
		this.initialDataFactory = initialDataFactory;
	}

	public Class<? extends View> getClazz() {
		return clazz;
	}

	public View getInstance() {
		return instance;
	}

	public BiFunction<Player, String[], Map<String, Object>> getInitialDataFactory() {
		return initialDataFactory;
	}
}