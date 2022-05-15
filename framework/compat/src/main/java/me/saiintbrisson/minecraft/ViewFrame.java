package me.saiintbrisson.minecraft;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public final class ViewFrame implements CompatViewFrame<ViewFrame> {

	@NotNull
	private final Plugin owner;

	private ViewErrorHandler errorHandler;

	@ToString.Exclude
	private final Map<Class<? extends View>, View> views = new HashMap<>();

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private Listener listener;
	private Function<PaginatedViewContext<?>, ViewItem> defaultPreviousPageItem, defaultNextPageItem;

	static {
		PlatformUtils.setFactory(new BukkitViewComponentFactory());
	}

	@Override
	public ViewFrame with(@NotNull View... views) {
		synchronized (getViews()) {
			for (final View view : views) {
				getViews().put(view.getClass(), view);
			}
		}
		return this;
	}

	@Override
	public void remove(@NotNull View... views) {
		synchronized (getViews()) {
			for (final View view : views) {
				view.close();
				getViews().remove(view.getClass());
			}
		}
	}

	/**
	 * @deprecated Use {@link #register()} and {@link #with(View...)} instead.
	 */
	@Deprecated
	public void register(@NotNull View... views) {
		with(views);
		register();
	}

	@Override
	public void register() {
		if (isRegistered())
			throw new IllegalStateException("This ViewFrame is already registered.");

		// check if there's another ViewFrame instance in the same server
		final ServicesManager servicesManager = getOwner().getServer().getServicesManager();
		if (servicesManager.isProvidedFor(ViewFrame.class))
			return;

		synchronized (views) {
			for (final View view : views.values()) {
				if (views.containsKey(view.getClass()))
					throw new IllegalArgumentException(String.format(
						"View %s already registered, try to use #with before #register instead.",
						view.getClass().getName()
					));

				views.put(view.getClass(), view);
			}
		}

		getOwner().getServer().getPluginManager().registerEvents(
			listener = new ViewListener(this),
			getOwner()
		);
		servicesManager.register(ViewFrame.class, this, getOwner(), ServicePriority.Normal);
	}

	@Override
	public void unregister() {
		if (!isRegistered())
			throw new IllegalStateException("Not registered");

		final Iterator<View> viewIterator = views.values().iterator();
		while (viewIterator.hasNext()) {
			final View view = viewIterator.next();
			view.close();
			viewIterator.remove();
		}

		HandlerList.unregisterAll(listener);
		listener = null;
	}

	@Override
	public boolean isRegistered() {
		return listener != null;
	}

	@Override
	public @NotNull ViewComponentFactory getFactory() {
		return PlatformUtils.getFactory();
	}

	@Override
	public <T extends View> T open(
		@NotNull Class<T> viewClass,
		@NotNull Player player
	) {
		return open(viewClass, player, Collections.emptyMap());
	}

	@Override
	public <T extends View> T open(
		@NotNull Class<T> viewClass,
		@NotNull Player player,
		@NotNull Map<String, Object> data
	) {
		if (!isRegistered())
			throw new IllegalStateException("Attempt to open a view without having registered the view frame.");

		@SuppressWarnings("unchecked") final T view = (T) views.get(viewClass);

		if (view == null)
			throw new IllegalStateException(String.format(
				"View %s is not registered.",
				viewClass.getName()
			));

		view.open(PlatformUtils.getFactory().createViewer(player), data);
		return view;
	}

	/**
	 * @deprecated Use {@link #with(View...)} instead.
	 */
	@Deprecated
	public void addView(final View view) {
		with(view);
	}

	/**
	 * @deprecated Use {@link #with(View...)} instead.
	 */
	@Deprecated
	public void addView(final View... views) {
		with(views);
	}

	public static ViewFrame of(
		@NotNull Plugin plugin,
		@NotNull View... views
	) {
		return new ViewFrame(plugin).with(views);
	}

}
