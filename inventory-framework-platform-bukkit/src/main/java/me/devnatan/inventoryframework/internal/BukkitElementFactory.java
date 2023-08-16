package me.devnatan.inventoryframework.internal;

import static java.util.Objects.requireNonNull;
import static me.devnatan.inventoryframework.runtime.util.InventoryUtils.checkInventoryTypeSupport;
import static me.devnatan.inventoryframework.util.IsTypeOf.isTypeOf;

import java.util.Map;
import java.util.UUID;
import me.devnatan.inventoryframework.*;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ComponentBuilder;
import me.devnatan.inventoryframework.context.*;
import me.devnatan.inventoryframework.logging.Logger;
import me.devnatan.inventoryframework.logging.NoopLogger;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitElementFactory extends ElementFactory {

    private static final ViewType defaultType = ViewType.CHEST;
    private Boolean worksInCurrentPlatform = null;

    @Override
    public @NotNull RootView createUninitializedRoot() {
        return new View();
    }

    // TODO Test it
    @Override
    public @NotNull ViewContainer createContainer(@NotNull IFContext context) {
        final ViewConfig config = context.getConfig();
        final ViewType finalType = config.getType() == null ? defaultType : config.getType();
        checkInventoryTypeSupport(finalType);

        final int size = finalType.normalize(config.getSize());
        if (size != 0 && !finalType.isExtendable())
            throw new IllegalArgumentException(String.format(
                    "Only \"%s\" type can have a custom size,"
                            + " \"%s\" always have a size of %d. Remove the parameter that specifies the size"
                            + " of the container on %s or just set the type explicitly.",
                    ViewType.CHEST.getIdentifier(),
                    finalType.getIdentifier(),
                    finalType.getMaxSize(),
                    context.getRoot().getClass().getName()));

        final InventoryHolder holder =
                context.getRoot() instanceof InventoryHolder ? (InventoryHolder) context.getRoot() : null;
        final Inventory inventory =
                InventoryFactory.current().createInventory(holder, finalType, size, config.getTitle());

        return new BukkitViewContainer(inventory, false, finalType);
    }

    @Override
    public @NotNull Viewer createViewer(Object... parameters) {
        final Object playerObject = parameters[0];
        if (!(playerObject instanceof Player))
            throw new IllegalArgumentException("createViewer(...) first parameter must be a Player");

        return new BukkitViewer((Player) playerObject);
    }

    @Override
    public @NotNull String convertViewer(Object input) {
        if (input instanceof String) return UUID.fromString((String) input).toString();
        if (input instanceof UUID) return input.toString();
        if (input instanceof Entity) return ((Entity) input).getUniqueId().toString();

        throw new IllegalArgumentException("Inconvertible viewer id: " + input);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFContext> @NotNull T createContext(
            @NotNull RootView root,
            ViewContainer container,
            Viewer subject,
            @NotNull Map<String, Viewer> viewers,
            @NotNull Class<T> kind,
            @Nullable IFContext parent,
            Object initialData) {
        if (isTypeOf(IFOpenContext.class, kind)) return (T) new OpenContext(root, subject, viewers, initialData);
        if (isTypeOf(IFRenderContext.class, kind))
            return (T) new me.devnatan.inventoryframework.context.RenderContext(
                    requireNonNull(parent).getId(),
                    root,
                    container,
                    subject,
                    viewers,
                    requireNonNull(parent).getConfig(),
                    initialData);
        if (isTypeOf(IFCloseContext.class, kind))
            return (T) new CloseContext(root, container, subject, viewers, requireNonNull(parent));

        throw new UnsupportedOperationException("Unsupported context kind: " + kind);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFSlotContext> @NotNull T createSlotContext(
            int slot,
            Component component,
            @NotNull ViewContainer container,
            Viewer subject,
            @NotNull Map<String, Viewer> viewers,
            @NotNull IFContext parent,
            @NotNull Class<?> kind) {
        if (isTypeOf(IFSlotRenderContext.class, kind))
            return (T) new SlotRenderContext(parent.getRoot(), container, subject, viewers, slot, parent, component);

        return (T) new SlotContext(parent.getRoot(), container, subject, viewers, slot, parent, component);
    }

    @Override
    public ComponentBuilder<?, Context> createComponentBuilder(@NotNull VirtualView root) {
        return new BukkitItemComponentBuilder(root);
    }

    @Override
    public synchronized boolean worksInCurrentPlatform() {
        if (worksInCurrentPlatform != null) return worksInCurrentPlatform;

        try {
            Class.forName("org.bukkit.Bukkit");
            worksInCurrentPlatform = true;
        } catch (ClassNotFoundException ignored) {
            // suppress ClassNotFoundException because it will be thrown in PlatformUtils
            worksInCurrentPlatform = false;
        }

        return worksInCurrentPlatform;
    }

    @Override
    public Logger getLogger() {
        return new NoopLogger();
    }

    @Override
    public Job scheduleJobInterval(@NotNull RootView root, long intervalInTicks, @NotNull Runnable execution) {
        final View platformRoot = (View) root;
        final ViewFrame platformFramework = (ViewFrame) platformRoot.getFramework();
        return new BukkitTaskJobImpl(platformFramework.getOwner(), intervalInTicks, execution);
    }
}
