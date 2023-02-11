package me.devnatan.inventoryframework;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.Getter;
import me.devnatan.inventoryframework.bukkit.listener.IFLibraryConflictWarningListener;
import me.devnatan.inventoryframework.bukkit.thirdparty.Metrics;
import me.devnatan.inventoryframework.feature.DefaultFeatureInstaller;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.feature.FeatureInstaller;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public final class ViewFrame implements IFViewFrame<ViewFrame> {

    private static final String BSTATS_SYSTEM_PROP = "inventory-framework.enable-bstats";
    private static final int BSTATS_PROJECT_ID = 15518;
    private static final String ROOT_PKG = "me.devnatan.inventoryframework";
    private static final String MAIN_PLUGIN_QNAME = "me.devnatan.inventoryframework.bukkit.InventoryFramework";

    private static final String RELOCATION_MESSAGE = "Inventory Framework is running as a shaded library"
            + " (in the same package as original package), there are chances of conflict if other "
            + "plugins are also using the library in different versions. It is recommended that "
            + "you relocate the library package to the same plugin package. Learn more about on docs: "
            + "https://github.com/DevNatan/inventory-framework/wiki/Installation#preventing-library-conflicts";

    @Getter(AccessLevel.PUBLIC)
    private final Plugin owner;

    private final Map<Class<? extends RootView>, RootView> registeredViews;
    private final FeatureInstaller<ViewFrame> featureInstaller;

    private boolean registered;

    private ViewFrame(@NotNull Plugin owner) {
        this.owner = owner;
        this.registeredViews = new HashMap<>();
        this.featureInstaller = new DefaultFeatureInstaller<>(this);
    }

    @Override
    public ViewFrame with(RootView... views) {
        synchronized (registeredViews) {
            for (final RootView view : views) {
                if (registeredViews.containsKey(view.getClass()))
                    throw new IllegalStateException(String.format(
                            "View %s already registered. Maybe your are using #register() before #with(...).",
                            view.getClass().getName()));

                registeredViews.put(view.getClass(), view);
            }
        }
        return this;
    }

    @Override
    public void remove(RootView... views) {
        synchronized (registeredViews) {
            for (final RootView view : views) {
                view.closeForEveryone();
                registeredViews.remove(view.getClass());
            }
        }
    }

    @Override
    public ViewFrame register() {
        if (isRegistered()) throw new IllegalStateException("This view frame is already registered.");

        tryEnableMetrics();
        initializeViews();
        registerListeners();
        return this;
    }

    private void tryEnableMetrics() {
        final ServicesManager servicesManager = getOwner().getServer().getServicesManager();
        if (servicesManager.isProvidedFor(IFViewFrame.class)) return;

        final boolean metricsEnabled =
                Boolean.parseBoolean(System.getProperty(BSTATS_SYSTEM_PROP, Boolean.TRUE.toString()));

        if (!metricsEnabled) return;

        if (!(getOwner() instanceof JavaPlugin)) {
            getOwner()
                    .getLogger()
                    .warning("InventoryFramework BStats metrics cannot be"
                            + " enabled since ViewFrame's owner is not a JavaPlugin.");
            return;
        }

        try {
            new Metrics((JavaPlugin) getOwner(), BSTATS_PROJECT_ID);
        } catch (final Exception ignored) {
        }
    }

    @SuppressWarnings("rawtypes")
    private void initializeViews() {
        for (final Map.Entry<Class<? extends RootView>, RootView> entry : registeredViews.entrySet()) {
            final Class<? extends RootView> clazz = entry.getKey();
            final RootView rootView = entry.getValue();
            if (!(rootView instanceof PlatformView))
                throw new IllegalStateException("Only PlatformView can be registered on this view frame");

            final PlatformView platformView = (PlatformView) rootView;
            try {
                platformView.internalInitialization();
                platformView.setInitialized(true);
            } catch (final RuntimeException exception) {
                platformView.setInitialized(false);
                getOwner()
                        .getLogger()
                        .severe(String.format(
                                "An error ocurred while enabling view %s: %s", clazz.getName(), exception));
            }
        }
    }

    private void registerListeners() {
        final Plugin plugin = getOwner();
        if (isShaded() && isLibrary()) {
            plugin.getLogger().warning(RELOCATION_MESSAGE);
            plugin.getServer()
                    .getPluginManager()
                    .registerEvents(new IFLibraryConflictWarningListener(RELOCATION_MESSAGE), plugin);
        }
    }

    @ApiStatus.Internal
    public boolean isShaded() {
        return !getOwner().getDescription().getMain().equals(MAIN_PLUGIN_QNAME);
    }

    @ApiStatus.Internal
    public boolean isLibrary() {
        return getClass().getPackage().getName().equals(ROOT_PKG);
    }

    @Override
    public void unregister() {
        if (!isRegistered()) return;

        // Locks new operations while unregistering
        registered = false;

        final Iterator<RootView> iterator = registeredViews.values().iterator();
        while (iterator.hasNext()) {
            final RootView view = iterator.next();
            try {
                view.closeForEveryone();
            } catch (final RuntimeException ignored) {
            }
            iterator.remove();
        }
    }

    @Override
    public boolean isRegistered() {
        return registered;
    }

    @Override
    public @NotNull ViewFrame getPlatform() {
        return this;
    }

    @Override
    public Collection<Feature<?, ?>> getInstalledFeatures() {
        return featureInstaller.getInstalledFeatures();
    }

    @Override
    public <C, R> @NotNull R install(@NotNull Feature<C, R> feature, @NotNull UnaryOperator<C> configure) {
        return featureInstaller.install(feature, configure);
    }

    @Override
    public void uninstall(@NotNull Feature<?, ?> feature) {
        featureInstaller.uninstall(feature);
    }

    /**
     * Creates a new ViewFrame.
     *
     * @param owner The plugin that owns this view frame.
     * @param views The views to be registered during creation.
     * @return A new ViewFrame instance.
     */
    public static @NotNull ViewFrame create(@NotNull Plugin owner, RootView... views) {
        return new ViewFrame(owner).with(views);
    }

    /**
     * Creates a new ViewFrame which is immediately registered.
     *
     * @param owner The plugin that owns this view frame.
     * @param views The views to be registered during creation.
     * @return A new registered ViewFrame instance.
     */
    public static @NotNull ViewFrame createRegistered(@NotNull Plugin owner, RootView... views) {
        return create(owner, views).register();
    }
}
