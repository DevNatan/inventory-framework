/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Matsubara
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.matsubara.roulette.util;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;

/**
 * A utility class for update the inventory of a player.
 * This is useful to change the title of an inventory.
 */

@SuppressWarnings("ConstantConditions")
public final class InventoryUpdate {

	// Classes.
	private final static Class<?> CRAFT_PLAYER_CLASS;
	private final static Class<?> CHAT_MESSAGE_CLASS;
	private final static Class<?> PACKET_PLAY_OUT_OPEN_WINDOW_CLASS;
	private final static Class<?> I_CHAT_BASE_COMPONENT_CLASS;
	private final static Class<?> CONTAINERS_CLASS;
	private final static Class<?> ENTITY_PLAYER_CLASS;
	private final static Class<?> CONTAINER_CLASS;

	// Methods.
	private final static MethodHandle getHandle;
	private final static MethodHandle getBukkitView;

	// Constructors.
	private static Constructor<?> chatMessageConstructor;
	private static Constructor<?> packetPlayOutOpenWindowConstructor;

	// Fields.
	private static Field activeContainerField;
	private static Field windowIdField;

	static {
		// Initialize classes.
		CRAFT_PLAYER_CLASS = ReflectionUtils.getCraftClass("entity.CraftPlayer");
		CHAT_MESSAGE_CLASS = ReflectionUtils.getNMSClass("network.chat", "ChatMessage");
		PACKET_PLAY_OUT_OPEN_WINDOW_CLASS = ReflectionUtils.getNMSClass("network.protocol.game", "PacketPlayOutOpenWindow");
		I_CHAT_BASE_COMPONENT_CLASS = ReflectionUtils.getNMSClass("network.chat", "IChatBaseComponent");
		// Check if we use containers, otherwise, can throw errors on older versions.
		CONTAINERS_CLASS = useContainers() ? ReflectionUtils.getNMSClass("world.inventory", "Containers") : null;
		ENTITY_PLAYER_CLASS = ReflectionUtils.getNMSClass("server.level", "EntityPlayer");
		CONTAINER_CLASS = ReflectionUtils.getNMSClass("world.inventory", "Container");

		MethodHandle handle = null, bukkitView = null;

		try {
			int version = ReflectionUtils.VER;
			MethodHandles.Lookup lookup = MethodHandles.lookup();

			// Initialize methods.
			handle = lookup.findVirtual(CRAFT_PLAYER_CLASS, "getHandle", MethodType.methodType(ENTITY_PLAYER_CLASS));
			bukkitView = lookup.findVirtual(CONTAINER_CLASS, "getBukkitView", MethodType.methodType(InventoryView.class));

			// Initialize constructors.
			chatMessageConstructor = CHAT_MESSAGE_CLASS.getConstructor(String.class, Object[].class);
			packetPlayOutOpenWindowConstructor =
				(useContainers()) ?
					PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructor(int.class, CONTAINERS_CLASS, I_CHAT_BASE_COMPONENT_CLASS) :
					// Older versions use Strings instead of containers, and require an int for the inventory size.
					PACKET_PLAY_OUT_OPEN_WINDOW_CLASS.getConstructor(int.class, String.class, I_CHAT_BASE_COMPONENT_CLASS, int.class);

			// Initialize fields.
			activeContainerField = (version == 17) ?
				ENTITY_PLAYER_CLASS.getField("bV") : (version == 18) ?
				ENTITY_PLAYER_CLASS.getField("bW") :
				ENTITY_PLAYER_CLASS.getField("activeContainer");
			windowIdField = (version > 16) ? CONTAINER_CLASS.getField("j") : CONTAINER_CLASS.getField("windowId");
		} catch (ReflectiveOperationException exception) {
			exception.printStackTrace();
		}

		getHandle = handle;
		getBukkitView = bukkitView;
	}

	/**
	 * Update the player inventory, so you can change the title.
	 *
	 * @param player   whose inventory will be updated.
	 * @param newTitle the new title for the inventory.
	 */

	public static void updateInventory(JavaPlugin plugin, Player player, String newTitle) {
		Validate.notNull(player, "Cannot update inventory to null player.");

		try {
			// Get EntityPlayer from CraftPlayer.
			Object craftPlayer = CRAFT_PLAYER_CLASS.cast(player);
			Object entityPlayer = getHandle.invoke(craftPlayer);

			if (newTitle != null && newTitle.length() > 32) {
				newTitle = newTitle.substring(0, 32);
			}

			// Create new title.
			Object title = chatMessageConstructor.newInstance(newTitle != null ? newTitle : "", new Object[]{});

			// Get activeContainer from EntityPlayer.
			Object activeContainer = activeContainerField.get(entityPlayer);

			// Get windowId from activeContainer.
			Integer windowId = (Integer) windowIdField.get(activeContainer);

			// Get InventoryView from activeContainer.
			Object bukkitView = getBukkitView.invoke(activeContainer);
			if (!(bukkitView instanceof InventoryView)) return;

			InventoryView view = (InventoryView) bukkitView;
			InventoryType type = view.getTopInventory().getType();

			// Workbenchs and anvils can change their title since 1.14.
			if ((type == InventoryType.WORKBENCH || type == InventoryType.ANVIL) && !useContainers()) return;

			// You can't reopen crafting, creative and player inventory.
			if (Arrays.asList("CRAFTING", "CREATIVE", "PLAYER").contains(type.name())) return;

			int size = view.getTopInventory().getSize();

			// Get container, check is not null.
			Containers container = Containers.getType(type, size);
			if (container == null) return;

			// If the container was added in a newer versions than the current, return.
			if (container.getContainerVersion() > ReflectionUtils.VER && useContainers()) {
				Bukkit.getLogger().warning(String.format(
					"[%s] This container doesn't work on your current version.",
					plugin.getDescription().getName()));
				return;
			}

			Object object;
			// Dispensers and droppers use the same container, but in previous versions, use a diferrent minecraft name.
			if (!useContainers() && container == Containers.GENERIC_3X3) {
				object = "minecraft:" + type.name().toLowerCase();
			} else {
				object = container.getObject();
			}

			// Create packet.
			Object packet =
				(useContainers()) ?
					packetPlayOutOpenWindowConstructor.newInstance(windowId, object, title) :
					packetPlayOutOpenWindowConstructor.newInstance(windowId, object, title, size);

			// Send packet sync.
			ReflectionUtils.sendPacketSync(player, packet);

			// Update inventory.
			player.updateInventory();
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	/**
	 * Containers were added in 1.14, a String were used in previous versions.
	 *
	 * @return whether to use containers.
	 */
	private static boolean useContainers() {
		return ReflectionUtils.VER > 13;
	}

	/**
	 * An enum class for the necessaries containers.
	 */
	private enum Containers {
		GENERIC_9X1(14, "minecraft:chest", "CHEST"),
		GENERIC_9X2(14, "minecraft:chest", "CHEST"),
		GENERIC_9X3(14, "minecraft:chest", "CHEST", "ENDER_CHEST", "BARREL"),
		GENERIC_9X4(14, "minecraft:chest", "CHEST"),
		GENERIC_9X5(14, "minecraft:chest", "CHEST"),
		GENERIC_9X6(14, "minecraft:chest", "CHEST"),
		GENERIC_3X3(14, null, "DISPENSER", "DROPPER"),
		ANVIL(14, "minecraft:anvil", "ANVIL"),
		BEACON(14, "minecraft:beacon", "BEACON"),
		BREWING_STAND(14, "minecraft:brewing_stand", "BREWING"),
		ENCHANTMENT(14, "minecraft:enchanting_table", "ENCHANTING"),
		FURNACE(14, "minecraft:furnace", "FURNACE"),
		HOPPER(14, "minecraft:hopper", "HOPPER"),
		MERCHANT(14, "minecraft:villager", "MERCHANT"),
		// For an unknown reason, when updating a shulker box, the size of the inventory get a little bigger.
		SHULKER_BOX(14, "minecraft:blue_shulker_box", "SHULKER_BOX"),

		// Added in 1.14, so only works with containers.
		BLAST_FURNACE(14, null, "BLAST_FURNACE"),
		CRAFTING(14, null, "WORKBENCH"),
		GRINDSTONE(14, null, "GRINDSTONE"),
		LECTERN(14, null, "LECTERN"),
		LOOM(14, null, "LOOM"),
		SMOKER(14, null, "SMOKER"),
		// CARTOGRAPHY in 1.14, CARTOGRAPHY_TABLE in 1.15 & 1.16 (container), handle in getObject().
		CARTOGRAPHY_TABLE(14, null, "CARTOGRAPHY"),
		STONECUTTER(14, null, "STONECUTTER"),

		// Added in 1.14, functional since 1.16.
		SMITHING(16, null, "SMITHING");

		private final int containerVersion;
		private final String minecraftName;
		private final String[] inventoryTypesNames;

		private final static char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

		Containers(int containerVersion, String minecraftName, String... inventoryTypesNames) {
			this.containerVersion = containerVersion;
			this.minecraftName = minecraftName;
			this.inventoryTypesNames = inventoryTypesNames;
		}

		/**
		 * Get the container based on the current open inventory of the player.
		 *
		 * @param type type of inventory.
		 * @return the container.
		 */
		public static Containers getType(InventoryType type, int size) {
			if (type == InventoryType.CHEST) {
				return Containers.valueOf("GENERIC_9X" + size / 9);
			}
			for (Containers container : Containers.values()) {
				for (String bukkitName : container.getInventoryTypesNames()) {
					if (bukkitName.equalsIgnoreCase(type.toString())) {
						return container;
					}
				}
			}
			return null;
		}

		/**
		 * Get the object from the container enum.
		 *
		 * @return a Containers object if 1.14+, otherwise, a String.
		 */
		public Object getObject() {
			try {
				if (!useContainers()) return getMinecraftName();
				int version = ReflectionUtils.VER;
				String name = (version == 14 && this == CARTOGRAPHY_TABLE) ? "CARTOGRAPHY" : name();
				// Since 1.17, containers go from "a" to "x".
				if (version > 16) name = String.valueOf(alphabet[ordinal()]);
				Field field = CONTAINERS_CLASS.getField(name);
				return field.get(null);
			} catch (ReflectiveOperationException exception) {
				exception.printStackTrace();
			}
			return null;
		}

		/**
		 * Get the version in which the inventory container was added.
		 *
		 * @return the version.
		 */
		public int getContainerVersion() {
			return containerVersion;
		}

		/**
		 * Get the name of the inventory from Minecraft for older versions.
		 *
		 * @return name of the inventory.
		 */
		public String getMinecraftName() {
			return minecraftName;
		}

		/**
		 * Get inventory types names of the inventory.
		 *
		 * @return bukkit names.
		 */
		public String[] getInventoryTypesNames() {
			return inventoryTypesNames;
		}
	}
}