package me.saiintbrisson.minecraft;

import static java.util.Objects.requireNonNull;
import static me.saiintbrisson.minecraft.AbstractView.CLICK;
import static me.saiintbrisson.minecraft.AbstractView.CLOSE;
import static me.saiintbrisson.minecraft.ViewItem.UNSET;
import static org.bukkit.Bukkit.createInventory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.devnatan.inventoryframework.IFContext;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.bukkit.BukkitIFContext;
import me.devnatan.inventoryframework.internal.platform.ViewContainer;
import me.devnatan.inventoryframework.internal.platform.Viewer;
import me.devnatan.inventoryframework.pipeline.Pipeline;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PACKAGE)
final class BukkitViewComponentFactory extends ViewComponentFactory {

    private Boolean worksInCurrentPlatform = null;

    @Override
    public @NotNull AbstractView createView(final int rows, final String title, final @NotNull ViewType type) {
        checkTypeSupport(type);
        return new View(rows, title, type);
    }

    @Override
    public void setupView(@NotNull AbstractView view) {
        registerInterceptors(view);
        getModifiers().values().forEach(modifier -> modifier.accept(view));
    }

    @Override
    public @NotNull ViewContainer createContainer(
            final @NotNull VirtualView view, final int size, final String title, final ViewType type) {
        final ViewType finalType = type == null ? AbstractView.DEFAULT_TYPE : type;
        checkTypeSupport(finalType);

        // only chests can have a custom size
        if (size != 0 && !finalType.isExtendable())
            throw new IllegalArgumentException(String.format(
                    "Only \"%s\" type can have a custom size,"
                            + " \"%s\" always have a size of %d. Remove the parameter that specifies the size"
                            + " of the container on %s or just set the type explicitly.",
                    ViewType.CHEST.getIdentifier(),
                    finalType.getIdentifier(),
                    finalType.getMaxSize(),
                    view.getClass().getName()));

        final Inventory inventory;
        if (title == null) {
            inventory = !finalType.isExtendable()
                    ? createInventory((InventoryHolder) view, requireNonNull(toInventoryType(finalType)))
                    : createInventory((InventoryHolder) view, size);
        } else if (!finalType.isExtendable())
            inventory = createInventory((InventoryHolder) view, requireNonNull(toInventoryType(finalType)), title);
        else inventory = createInventory((InventoryHolder) view, size, title);

        return new BukkitViewContainer(inventory);
    }

    @Override
    public @NotNull Viewer createViewer(Object... parameters) {
        final Object playerObject = parameters[0];
        if (!(playerObject instanceof Player))
            throw new IllegalArgumentException("createViewer(...) first parameter must be a Player");

        return new BukkitViewer((Player) playerObject);
    }

    @Override
    public @NotNull BaseViewContext createContext(
            @NotNull AbstractView root,
            @NotNull ViewContainer container,
            @Nullable Class<? extends IFContext> backingContext,
            @Nullable Viewer viewer) {
        if (backingContext != null && OpenViewContext.class.isAssignableFrom(backingContext) && viewer != null)
            return new OpenViewContext(root, container, ((BukkitViewer) viewer).getPlayer());

        return root instanceof PaginatedView
                ? new PaginatedViewContextImpl<>(root, container)
                : new ViewContextImpl(root, container);
    }

    @Override
    @NotNull
    public AbstractViewSlotContext createSlotContext(
            int slot, ViewItem item, IFContext parent, ViewContainer container, int index, Object value) {
        final Player player = ((BukkitIFContext) parent).getPlayer();
        return index == UNSET
                ? new ViewSlotContext(slot, item, parent, container, player)
                : new BukkitPaginatedViewSlotContextImpl<>(index, value, slot, item, parent, container, player);
    }

    @Override
    public synchronized boolean worksInCurrentPlatform() {
        if (worksInCurrentPlatform != null) return worksInCurrentPlatform;

        try {
            Class.forName("org.bukkit.Bukkit");
            worksInCurrentPlatform = true;
        } catch (ClassNotFoundException ignored) {
            // suppress RuntimeException because it will be thrown in PlatformUtils
            worksInCurrentPlatform = false;
        }

        return worksInCurrentPlatform;
    }

    @Override
    public Object createItem(@Nullable Object stack) {
        if (stack instanceof ItemStack) return ((ItemStack) stack).clone();
        if (stack instanceof Material) return new ItemStack((Material) stack);
        if (stack == null) return null;

        throw new IllegalArgumentException(String.format(
                "Unsupported item type \"%s\": %s", stack.getClass().getName(), stack));
    }

    private InventoryType toInventoryType(@NotNull ViewType type) {
        if (type == ViewType.HOPPER) return InventoryType.HOPPER;
        if (type == ViewType.FURNACE) return InventoryType.FURNACE;
        if (type == ViewType.CHEST) return InventoryType.CHEST;

        return null;
    }

    private void checkTypeSupport(@NotNull ViewType type) {
        if (toInventoryType(type) != null) return;

        throw new IllegalArgumentException(
                String.format("%s view type is not supported on Bukkit platform.", type.getIdentifier()));
    }

    private void registerInterceptors(AbstractView view) {
        final Pipeline<? super IFContext> pipeline = view.getPipeline();
        pipeline.intercept(CLICK, new ItemClickInterceptor());
        pipeline.intercept(CLICK, new GlobalClickInterceptor());
        pipeline.intercept(CLICK, new GlobalClickOutsideInterceptor());
        pipeline.intercept(CLICK, new GlobalHotbarClickInterceptor());
        pipeline.intercept(CLICK, new GlobalItemHoldInterceptor());
        pipeline.intercept(CLICK, new CloseMarkInterceptor());
		pipeline.intercept(CLOSE, new CloseHandlerInterceptor());
    }
}
