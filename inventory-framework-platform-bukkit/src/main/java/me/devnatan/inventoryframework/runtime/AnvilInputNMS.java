package me.devnatan.inventoryframework.runtime;

import static me.devnatan.inventoryframework.IFDebug.debug;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.CONTAINER;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.ENTITY_PLAYER;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.createTitleComponent;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.getConstructor;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.getContainerOrName;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.getField;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.getMethod;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.packetPlayOutOpenWindow;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.setField;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.setFieldHandle;
import static me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate.useContainers;
import static me.devnatan.inventoryframework.runtime.thirdparty.ReflectionUtils.getNMSClass;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;
import me.devnatan.inventoryframework.runtime.thirdparty.InventoryUpdate;
import me.devnatan.inventoryframework.runtime.thirdparty.ReflectionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class AnvilInputNMS {

    // CONSTRUCTORS
    private static final MethodHandle ANVIL_CONSTRUCTOR;

    // METHODS
    private static final MethodHandle GET_PLAYER_NEXT_CONTAINER_COUNTER;
    private static final MethodHandle GET_PLAYER_INVENTORY;
    private static final MethodHandle SET_PLAYER_ACTIVE_CONTAINER;

    // FIELDS
    private static final MethodHandle CONTAINER_CHECK_REACHABLE;
    private static final MethodHandle PLAYER_DEFAULT_CONTAINER;
    private static final MethodHandle CONTAINER_WINDOW_ID;

    static {
        try {
            final InventoryUpdate.Containers anvil = InventoryUpdate.Containers.ANVIL;

            debug(
                    "Detected anvil container as %s at %s",
                    anvil.getMinecraftName(),
                    InventoryUpdate.Containers.ANVIL.getObject().getClass().getName());

            final Class<?> playerInventoryClass = getNMSClass("world.entity.player", "PlayerInventory");
            ANVIL_CONSTRUCTOR =
                    getConstructor(getNMSClass("world.inventory", "ContainerAnvil"), int.class, playerInventoryClass);
            CONTAINER_CHECK_REACHABLE = setFieldHandle(CONTAINER, boolean.class, "checkReachable");

            final Class<?> containerPlayer = getNMSClass("world.inventory", "ContainerPlayer");
            PLAYER_DEFAULT_CONTAINER = getField(ENTITY_PLAYER, containerPlayer, "inventoryMenu", "bQ");
            SET_PLAYER_ACTIVE_CONTAINER =
                    setField(ENTITY_PLAYER, containerPlayer, "activeContainer", "bR", "containerMenu");
            GET_PLAYER_NEXT_CONTAINER_COUNTER =
                    getMethod(ENTITY_PLAYER, "nextContainerCounter", MethodType.methodType(int.class));
            GET_PLAYER_INVENTORY = getMethod(ENTITY_PLAYER, "fN", MethodType.methodType(playerInventoryClass));
            CONTAINER_WINDOW_ID = setField(CONTAINER, int.class, "windowId", "containerId", "j");
        } catch (Exception exception) {
            throw new RuntimeException(
                    "Unsupported version for Anvil Input feature: " + ReflectionUtils.getVersionInformation(),
                    exception);
        }
    }

    private AnvilInputNMS() {}

    public static void open(Player player) {
        try {
            resetActiveContainer(player);

            final Object entityPlayer = ReflectionUtils.getEntityPlayer(player);
            final int windowId = (int) GET_PLAYER_NEXT_CONTAINER_COUNTER.invoke(entityPlayer);
            final Object anvilContainer = ANVIL_CONSTRUCTOR.invoke(windowId, GET_PLAYER_INVENTORY.invoke(entityPlayer));

            CONTAINER_CHECK_REACHABLE.invoke(anvilContainer, false);

            final Inventory inventory =
                    ((InventoryView) InventoryUpdate.getBukkitView.invoke(anvilContainer)).getTopInventory();
            inventory.setItem(0, new ItemStack(Material.DIAMOND));

            final Object title = createTitleComponent("abc");
            Object nmsContainers = getContainerOrName(InventoryUpdate.Containers.ANVIL, InventoryType.ANVIL);
            Object openWindowPacket = useContainers()
                    ? packetPlayOutOpenWindow.invoke(windowId, nmsContainers, title)
                    : packetPlayOutOpenWindow.invoke(windowId, nmsContainers, title, 0);

            ReflectionUtils.sendPacketSync(player, openWindowPacket);
            SET_PLAYER_ACTIVE_CONTAINER.invoke(entityPlayer, anvilContainer);
            CONTAINER_WINDOW_ID.invoke(anvilContainer, windowId);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void resetActiveContainer(Player player) throws Throwable {
        final Object entityPlayer = ReflectionUtils.getEntityPlayer(player);
        final Object defaultContainer = PLAYER_DEFAULT_CONTAINER.invoke(entityPlayer);

        SET_PLAYER_ACTIVE_CONTAINER.invoke(entityPlayer, defaultContainer);
    }
}
