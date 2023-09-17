package me.devnatan.inventoryframework;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;
import me.devnatan.inventoryframework.feature.DefaultFeatureInstaller;
import me.devnatan.inventoryframework.feature.Feature;
import me.devnatan.inventoryframework.feature.FeatureInstaller;
import me.devnatan.inventoryframework.internal.BukkitElementFactory;
import me.devnatan.inventoryframework.internal.PlatformUtils;
import me.devnatan.inventoryframework.runtime.thirdparty.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

public class ViewFrame extends IFViewFrame<ViewFrame, View> implements FeatureInstaller<ViewFrame> {

    private static final String BSTATS_SYSTEM_PROP = "inventory-framework.enable-bstats";
    private static final int BSTATS_PROJECT_ID = 15518;
    private static final String PLUGIN_FQN = "me.devnatan.inventoryframework.runtime.InventoryFramework";

    private static final String RELOCATION_MESSAGE =
            "Inventory Framework is running as a shaded non-relocated library. It's extremely recommended that "
                    + "you relocate the library package. Learn more about on docs: "
                    + "https://github.com/DevNatan/inventory-framework/wiki/Installation#preventing-library-conflicts";

    private final Plugin owner;
    private final FeatureInstaller<ViewFrame> featureInstaller = new DefaultFeatureInstaller<>(this);

    static {
        PlatformUtils.setFactory(new BukkitElementFactory());
    }

    private ViewFrame(Plugin owner) {
        this.owner = owner;
    }

    @NotNull
    public final Plugin getOwner() {
        return owner;
    }

    /**
     * Opens a view to a player.
     *
     * @param viewClass The target view to be opened.
     * @param player    The player that the view will be open to.
     */
    public final void open(@NotNull Class<? extends View> viewClass, @NotNull Player player) {
        open(viewClass, player, null);
    }

    /**
     * Opens a view to a player with initial data.
     *
     * @param viewClass   The target view to be opened.
     * @param player      The player that the view will be open to.
     * @param initialData The initial data.
     */
    public final void open(@NotNull Class<? extends View> viewClass, @NotNull Player player, Object initialData) {
        open(viewClass, Collections.singletonList(player), initialData);
    }

    /**
     * Opens a view to more than one player.
     * <p>
     * These players will see the same inventory and share the same context.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param viewClass The target view to be opened.
     * @param players   The players that the view will be open to.
     */
    @ApiStatus.Experimental
    public final void open(@NotNull Class<? extends View> viewClass, @NotNull Collection<? extends Player> players) {
        open(viewClass, players, null);
    }

    /**
     * Opens a view to more than one player with initial data.
     * <p>
     * These players will see the same inventory and share the same context.
     *
     * <p><b><i> This API is experimental and is not subject to the general compatibility guarantees
     * such API may be changed or may be removed completely in any further release. </i></b>
     *
     * @param viewClass   The target view to be opened.
     * @param players     The players that the view will be open to.
     * @param initialData The initial data.
     */
    @ApiStatus.Experimental
    public final void open(
            @NotNull Class<? extends View> viewClass,
            @NotNull Collection<? extends Player> players,
            Object initialData) {
        internalOpen(viewClass, players, initialData);
    }

    @Override
    public final ViewFrame register() {
        if (isRegistered()) throw new IllegalStateException("This view frame is already registered");

        tryEnableMetrics();
        checkRelocationIssues();
        initializeViews();
        getOwner().getServer().getPluginManager().registerEvents(new IFInventoryListener(this), getOwner());
        setRegistered(true);
        getPipeline().execute(IFViewFrame.FRAME_REGISTERED, this);
        return this;
    }

    @Override
    public final void unregister() {
        if (!isRegistered()) return;

        // Locks new operations while unregistering
        setRegistered(false);

        final Iterator<View> iterator = registeredViews.values().iterator();
        while (iterator.hasNext()) {
            final View view = iterator.next();
            try {
                view.closeForEveryone();
            } catch (final RuntimeException ignored) {
            }
            iterator.remove();
        }
        getPipeline().execute(IFViewFrame.FRAME_UNREGISTERED, this);
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

    @SuppressWarnings("CallToPrintStackTrace")
    private void initializeViews() {
        for (final Map.Entry<UUID, View> entry : getRegisteredViews().entrySet()) {
            final View view = entry.getValue();

            try {
                view.internalInitialization(this);
                view.setInitialized(true);
            } catch (final RuntimeException exception) {
                view.setInitialized(false);
                getOwner()
                        .getLogger()
                        .severe(String.format(
                                "An error occurred while enabling view %s: %s",
                                view.getClass().getName(), exception));
                exception.printStackTrace();
            }
        }
    }

    private void checkRelocationIssues() {
        final Plugin plugin = getOwner();
        final boolean isLibraryAsPluginAvailable =
                getOwner().getServer().getPluginManager().getPlugin("InventoryFramework") != null;
        boolean isLibraryPresent = false;
        try {
            Class.forName(PLUGIN_FQN);
            isLibraryPresent = true;
        } catch (ClassNotFoundException ignored) {
        }

        if (!isLibraryAsPluginAvailable && isLibraryPresent)
            plugin.getLogger().warning(RELOCATION_MESSAGE);
    }

    /**
     * <b><i> This is an internal inventory-framework API that should not be used from outside of
     * this library. No compatibility guarantees are provided. </i></b>
     */
    @ApiStatus.Internal
    public final Viewer getViewer(@NotNull Player player) {
        return viewerById.get(player.getUniqueId().toString());
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
    public final @NotNull ViewFrame getPlatform() {
        return this;
    }

    @Override
    public final Collection<Feature<?, ?, ViewFrame>> getInstalledFeatures() {
        return featureInstaller.getInstalledFeatures();
    }

    @Override
    public final <C, R> @NotNull R install(
            @NotNull Feature<C, R, ViewFrame> feature, @NotNull UnaryOperator<C> configure) {
        final R value = featureInstaller.install(feature, configure);
        IFDebug.debug("Feature %s installed", feature.name());
        return value;
    }

    @Override
    public final void uninstall(@NotNull Feature<?, ?, ViewFrame> feature) {
        featureInstaller.uninstall(feature);
        IFDebug.debug("Feature %s uninstalled", feature.name());
    }
}
