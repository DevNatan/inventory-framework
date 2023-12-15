package me.devnatan.inventoryframework.internal;

import static me.devnatan.inventoryframework.runtime.util.InventoryUtils.checkInventoryTypeSupport;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import me.devnatan.inventoryframework.BukkitViewContainer;
import me.devnatan.inventoryframework.BukkitViewer;
import me.devnatan.inventoryframework.PlatformView;
import me.devnatan.inventoryframework.RootView;
import me.devnatan.inventoryframework.View;
import me.devnatan.inventoryframework.ViewConfig;
import me.devnatan.inventoryframework.ViewContainer;
import me.devnatan.inventoryframework.ViewFrame;
import me.devnatan.inventoryframework.ViewType;
import me.devnatan.inventoryframework.Viewer;
import me.devnatan.inventoryframework.VirtualView;
import me.devnatan.inventoryframework.component.BukkitItemComponentBuilder;
import me.devnatan.inventoryframework.component.Component;
import me.devnatan.inventoryframework.component.ItemComponentBuilder;
import me.devnatan.inventoryframework.context.CloseContext;
import me.devnatan.inventoryframework.context.IFCloseContext;
import me.devnatan.inventoryframework.context.IFContext;
import me.devnatan.inventoryframework.context.IFOpenContext;
import me.devnatan.inventoryframework.context.IFRenderContext;
import me.devnatan.inventoryframework.context.IFSlotClickContext;
import me.devnatan.inventoryframework.context.OpenContext;
import me.devnatan.inventoryframework.context.RenderContext;
import me.devnatan.inventoryframework.context.SlotClickContext;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitElementFactory extends ElementFactory {

    private static final ViewType defaultType = ViewType.CHEST;
    private Boolean worksInCurrentPlatform = null;


    @Override
    public ViewContainer createContainer(IFContext context) {
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

        return new BukkitViewContainer(inventory, finalType, false, false);
    }

    @Override
    public Viewer createViewer(Object entity, IFRenderContext context) {
        if (!(entity instanceof Player))
            throw new IllegalArgumentException("createViewer(...) first parameter must be a Player");

        return new BukkitViewer((Player) entity, context);
    }

    @Override
    public IFOpenContext createOpenContext(
		RootView root, Viewer subject, List<Viewer> viewers, Object initialData) {
        return new OpenContext(
                (View) root,
                subject,
                viewers.stream().collect(Collectors.toMap(Viewer::getId, Function.identity())),
                initialData);
    }

    @Override
    public IFRenderContext createRenderContext(
            UUID id,
            RootView root,
            ViewConfig config,
            ViewContainer container,
            Map<String, Viewer> viewers,
            Viewer subject,
            Object initialData) {
        return new RenderContext(id, (View) root, config, container, viewers, subject, initialData);
    }

    @Override
    public IFSlotClickContext createSlotClickContext(
            int slotClicked,
            Viewer whoClicked,
            ViewContainer interactionContainer,
            Component componentClicked,
            Object origin,
            boolean combined) {
        final IFRenderContext context = whoClicked.getActiveContext();
        return new SlotClickContext(
                slotClicked,
                context,
                whoClicked,
                interactionContainer,
                componentClicked,
                (InventoryClickEvent) origin,
                combined);
    }

    @Override
    public IFCloseContext createCloseContext(Viewer viewer, IFRenderContext parent) {
        return new CloseContext(viewer, parent);
    }

    @Override
    public ItemComponentBuilder createItemComponentBuilder(VirtualView root) {
        return new BukkitItemComponentBuilder();
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
    public Job scheduleJobInterval(IFContext context, long intervalInTicks, Runnable execution) {
		@SuppressWarnings("rawtypes")
		final PlatformView root = (PlatformView) context.getRoot();
		final Plugin plugin = ((ViewFrame) root.getFramework()).getOwner();

        return new BukkitTaskJobImpl(plugin, intervalInTicks, execution);
    }
}
