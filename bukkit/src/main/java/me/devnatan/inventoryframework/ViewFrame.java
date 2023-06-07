package me.devnatan.inventoryframework;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devnatan.inventoryframework.feature.DefaultFeatureInstaller;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.feature.FeatureInstaller;
import me.devnatan.inventoryframework.internal.BukkitElementFactory;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import me.devnatan.inventoryframework.runtime.BukkitViewer;
import me.devnatan.inventoryframework.runtime.listener.IFInventoryListener;
import me.devnatan.inventoryframework.runtime.listener.IFLibraryConflictWarningListener;
import me.devnatan.inventoryframework.runtime.thirdparty.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

@VisibleForTesting
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ViewFrame extends IFViewFrame<ViewFrame> implements FeatureInstaller<ViewFrame> {

    private static final String BSTATS_SYSTEM_PROP = "inventory-framework.enable-bstats";
    private static final int BSTATS_PROJECT_ID = 15518;
    private static final String ROOT_PKG = "me.devnatan.inventoryframework";
    private static final String MAIN_PLUGIN_QNAME = "me.devnatan.inventoryframework.runtime.InventoryFramework";

    private static final String RELOCATION_MESSAGE =
            "Inventory Framework is running as a shaded non-relocated library. It's extremely recommended that "
                    + "you relocate the library package. Learn more about on docs: "
                    + "https://github.com/DevNatan/inventory-framework/wiki/Installation#preventing-library-conflicts";

    @Getter(AccessLevel.PUBLIC)
    private final @NotNull Plugin owner;

    private final FeatureInstaller<ViewFrame> featureInstaller = new DefaultFeatureInstaller<>(this);

    static {
        PlatformUtils.setFactory(new BukkitElementFactory());
    }

    @Override
    public void open(@NotNull Class<? extends RootView> viewClass, @NotNull Viewer viewer, Object initialData) {
        if (!(viewer instanceof BukkitViewer))
            throw new IllegalArgumentException("Only BukkitViewer viewer impl is supported");

        final RootView view = getRegisteredViewByType(viewClass);
        if (!(view instanceof PlatformView))
            throw new IllegalStateException("Only PlatformView can be opened through #open(...)");

        view.open(viewer, initialData);
    }

    /**
     * Opens a view to a player.
     *
     * @param viewClass The target view to be opened.
     * @param player    The player that the view will be open to.
     */
    public void open(@NotNull Class<? extends RootView> viewClass, @NotNull Player player) {
        open(viewClass, PlatformUtils.getFactory().createViewer(player), null);
    }

    /**
     * Opens a view to a player with initial data.
     *
     * @param viewClass The target view to be opened.
     * @param player    The player that the view will be open to.
     * @param initialData The initial data.
     */
    public void open(
            @NotNull Class<? extends RootView> viewClass, @NotNull Player player, @NotNull Object initialData) {
        open(viewClass, PlatformUtils.getFactory().createViewer(player), initialData);
    }

    @Override
    public ViewFrame register() {
        if (isRegistered()) throw new IllegalStateException("This view frame is already registered");

        tryEnableMetrics();
        initializeViews();
        registerListeners();
        setRegistered(true);
        return this;
    }

    @Override
    public void unregister() {
        if (!isRegistered()) return;

        // Locks new operations while unregistering
        setRegistered(false);

        final Iterator<RootView> iterator = getRegisteredViews().values().iterator();
        while (iterator.hasNext()) {
            final RootView view = iterator.next();
            try {
                view.closeForEveryone();
            } catch (final RuntimeException ignored) {
            }
            iterator.remove();
        }
    }

    @ApiStatus.Internal
    public boolean isLibraryAsPlugin() {
        return getOwner().getDescription().getMain().equals(MAIN_PLUGIN_QNAME);
    }

    @ApiStatus.Internal
    public boolean isShaded() {
        return getClass().getPackage().getName().equals(ROOT_PKG);
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
        for (final Map.Entry<UUID, RootView> entry : getRegisteredViews().entrySet()) {
            final RootView rootView = entry.getValue();
            if (!(rootView instanceof PlatformView))
                throw new IllegalStateException("Only PlatformView can be registered on this view frame");

            final PlatformView platformView = (PlatformView) rootView;
            try {
                platformView.internalInitialization(this);
                platformView.setInitialized(true);
            } catch (final RuntimeException exception) {
                platformView.setInitialized(false);
                getOwner()
                        .getLogger()
                        .severe(String.format(
                                "An error occurred while enabling view %s: %s",
                                rootView.getClass().getName(), exception));
                exception.printStackTrace();
            }
        }
    }

    private void registerListeners() {
        final Plugin plugin = getOwner();
        if (!isLibraryAsPlugin() && isShaded()) {
            plugin.getLogger().warning(RELOCATION_MESSAGE);
            plugin.getServer()
                    .getPluginManager()
                    .registerEvents(new IFLibraryConflictWarningListener(RELOCATION_MESSAGE), plugin);
        }

        plugin.getServer().getPluginManager().registerEvents(new IFInventoryListener(this), plugin);
    }

    /**
     * Returns the current {@link RootView} the player is viewing based on open inventory.
     * <p>
     * Only views registered in that view frame are returned.
     *
     * @param player The player.
     * @return The current view the player is viewing or {@code null} if it was not found, or it was
     * not possible to determine it.
     */
    public RootView getCurrentView(@NotNull Player player) {
        final Inventory topInventory = player.getOpenInventory().getTopInventory();
        if (!(topInventory.getHolder() instanceof View)) return null;

        final View view = (View) topInventory.getHolder();
        if (!getRegisteredViews().containsKey(view.getUniqueId())) return null;

        return view;
    }

    /**
     * Creates a new ViewFrame.
     *
     * @param owner The plugin that owns this view frame.
     * @return A new ViewFrame instance.
     */
    public static @NotNull ViewFrame create(@NotNull Plugin owner) {
        return new ViewFrame(owner);
    }

    @Override
    public @NotNull ViewFrame getPlatform() {
        return this;
    }

    @Override
    public Collection<Feature<?, ?, ViewFrame>> getInstalledFeatures() {
        return featureInstaller.getInstalledFeatures();
    }

    @Override
    public <C, R> @NotNull R install(@NotNull Feature<C, R, ViewFrame> feature, @NotNull UnaryOperator<C> configure) {
        return featureInstaller.install(feature, configure);
    }

    @Override
    public void uninstall(@NotNull Feature<?, ?, ViewFrame> feature) {
        featureInstaller.uninstall(feature);
    }
}
