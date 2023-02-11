package me.devnatan.inventoryframework.internal;

import static java.util.Objects.requireNonNull;
import static me.devnatan.inventoryframework.bukkit.util.InventoryUtils.checkInventoryTypeSupport;
import static me.devnatan.inventoryframework.bukkit.util.InventoryUtils.toInventoryType;
import static me.devnatan.inventoryframework.util.IsTypeOf.isTypeOf;
import static org.bukkit.Bukkit.createInventory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.IFItem;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotContext;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.devnatan.inventoryframework.logging.Logger;
import me.devnatan.inventoryframework.logging.NoopLogger;
import me.devnatan.inventoryframework.pipeline.GlobalClickInterceptor;
import me.devnatan.inventoryframework.pipeline.ItemClickInterceptor;
import me.devnatan.inventoryframework.pipeline.OpenInterceptor;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import me.devnatan.inventoryframework.pipeline.StandardPipelinePhases;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class BukkitElementFactory extends ElementFactory {

    private static final ViewType defaultType = ViewType.CHEST;
    private Boolean worksInCurrentPlatform = null;

    @Override
    public @NotNull RootView createUninitializedRoot() {
        return new View();
    }

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

        // TODO check current server version to determine if will use InventoryHolder or not
        final Inventory inventory;
        if (title == null) {
            inventory = !finalType.isExtendable()
                    ? createInventory(null, requireNonNull(toInventoryType(finalType)))
                    : createInventory(null, size);
        } else if (!finalType.isExtendable()) {
            inventory = createInventory(null, requireNonNull(toInventoryType(finalType)), title);
        } else {
            inventory = createInventory(null, size, title);
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

    @Override
    public @NotNull IFContext createContext(
            @NotNull RootView root,
            ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull Class<? extends IFContext> kind,
            boolean shared) {
        if (shared) throw new IllegalStateException("Shared contexts are not yet supported");
        if (isTypeOf(IFOpenContext.class, kind)) return new OpenContext(root, viewer);
        if (isTypeOf(IFRenderContext.class, kind)) return new RenderContext(root, container, viewer);
        if (isTypeOf(IFCloseContext.class, kind)) return new CloseContext(root, container, viewer);

        throw new UnsupportedOperationException("Unsupported context kind: " + kind);
    }

    @Override
    public @NotNull IFSlotContext createSlotContext(
            int slot,
            IFItem<?> internalItem,
            @NotNull ViewContainer container,
            @NotNull Viewer viewer,
            @NotNull IFContext parent) {
        return new SlotContext(parent.getRoot(), container, viewer, slot, parent, internalItem);
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

    @Override
    public void registerPlatformInterceptors(@NotNull RootView view) {
        super.registerPlatformInterceptors(view);
        final Pipeline<? super IFContext> pipeline = view.getPipeline();
        pipeline.intercept(StandardPipelinePhases.OPEN, new OpenInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new ItemClickInterceptor());
        pipeline.intercept(StandardPipelinePhases.CLICK, new GlobalClickInterceptor());
    }
}
