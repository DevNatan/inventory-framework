package me.devnatan.inventoryframework.internal;

import static java.util.Objects.requireNonNull;
import static me.devnatan.inventoryframework.bukkit.util.InventoryUtils.checkInventoryTypeSupport;
import static me.devnatan.inventoryframework.bukkit.util.InventoryUtils.toInventoryType;
import static me.devnatan.inventoryframework.util.IsTypeOf.isTypeOf;
import static org.bukkit.Bukkit.createInventory;

import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.bukkit.BukkitViewContainer;
import me.devnatan.inventoryframework.bukkit.BukkitViewer;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.logging.Logger;
import me.devnatan.inventoryframework.logging.NoopLogger;
import me.devnatan.inventoryframework.pipeline.GlobalClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ItemClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ItemCloseOnClickInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BukkitElementFactory extends ElementFactory {

    private static final ViewType defaultType = ViewType.CHEST;
    private Boolean worksInCurrentPlatform = null;

    @Override
    public @NotNull RootView createUninitializedRoot() {
        return new View();
    }

    // TODO Test it
    @Override
    public @NotNull ViewContainer createContainer(
            @NotNull IFContext context, int size, @Nullable String title, @Nullable ViewType type) {
        final ViewType finalType = type == null ? defaultType : type;
        checkInventoryTypeSupport(finalType);

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

        final Inventory inventory;
        final String finalTitle = context.getTitle().isEmpty() ? null : context.getTitle();
        if (finalTitle == null) {
            inventory = !finalType.isExtendable() || size == 0
                    ? createInventory(holder, requireNonNull(toInventoryType(finalType)))
                    : createInventory(holder, size);
        } else if (!finalType.isExtendable()) {
            inventory = createInventory(holder, requireNonNull(toInventoryType(finalType)), finalTitle);
        } else {
            inventory = createInventory(holder, size, finalTitle);
        }

        return new BukkitViewContainer(inventory, false);
    }

    @Override
    public @NotNull Viewer createViewer(Object... parameters) {
        final Object playerObject = parameters[0];
        if (!(playerObject instanceof Player))
            throw new IllegalArgumentException("createViewer(...) first parameter must be a Player");

        return new BukkitViewer((Player) playerObject);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IFContext> @NotNull T createContext(
            @NotNull RootView root,
            ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull Class<T> kind,
            boolean shared,
            @Nullable IFContext parent) {
        if (shared) throw new IllegalStateException("Shared contexts are not yet supported");
        if (isTypeOf(IFOpenContext.class, kind)) return (T) new OpenContext(root, viewer);
        if (isTypeOf(IFRenderContext.class, kind)) return (T) new RenderContext(root, container, viewer, parent);
        if (isTypeOf(IFCloseContext.class, kind)) return (T) new CloseContext(root, container, viewer, parent);

        throw new UnsupportedOperationException("Unsupported context kind: " + kind);
    }

    @Override
    public @NotNull IFSlotContext createSlotContext(
            int slot,
            Component component,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull IFContext parent) {
        return new SlotContext(parent.getRoot(), container, viewer, slot, parent, component);
    }

    @Override
    public Object createItem(@Nullable Object stack) {
        if (stack instanceof ItemStack) return ((ItemStack) stack).clone();
        if (stack instanceof Material) return new ItemStack((Material) stack);
        if (stack == null) return null;

        throw new IllegalArgumentException(String.format(
                "Unsupported Bukkit item type \"%s\": %s", stack.getClass().getName(), stack));
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

    @SuppressWarnings("rawtypes")
    @Override
    public void registerPlatformInterceptors(@NotNull PlatformView view) {
        final Pipeline<? super IFContext> pipeline = view.getPipeline();
        pipeline.intercept(StandardPipelinePhases.CLICK, new ItemClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new GlobalClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new ItemCloseOnClickInterceptor());
    }
}
