package me.devnatan.inventoryframework

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.PlayerInventory
import net.minestom.server.item.ItemStack
import java.util.*

class MinestomViewContainer(
    private val inventory: Inventory, shared: Boolean, private val type: ViewType,
    private val proxied: Boolean
) :
    ViewContainer {
    val isShared: Boolean = shared

    fun getInventory(): Inventory {
        return inventory
    }

    override fun isProxied(): Boolean {
        return proxied
    }

    override fun getTitle(): String {
        val diffTitle: Boolean = inventory.viewers.stream()
            .map { player ->
                (player.openInventory as? Inventory)?.title
                    ?.let { PlainTextComponentSerializer.plainText().serialize(it) } ?: ""
            }
            .distinct()
            .findAny()
            .isPresent

        check(!(diffTitle && isShared)) { "Cannot get unique title of shared inventory" }
        val openInventory = inventory.viewers.first().openInventory
        return (openInventory as? Inventory)?.title
            ?.let { PlainTextComponentSerializer.plainText().serialize(it) } ?: ""
    }

    override fun getTitle(viewer: Viewer): String {
        return ((viewer as MinestomViewer).player.openInventory as? Inventory)?.title
            ?.let { PlainTextComponentSerializer.plainText().serialize(it) } ?: ""
    }

    override fun getType(): ViewType {
        return type
    }

    override fun getRowsCount(): Int {
        return size / columnsCount
    }

    override fun getColumnsCount(): Int {
        return type.columns
    }

    override fun renderItem(slot: Int, item: Any) {
        requireSupportedItem(item)
        inventory.setItemStack(slot, item as ItemStack)
    }

    override fun removeItem(slot: Int) {
        inventory.setItemStack(slot, ItemStack.AIR)
    }

    override fun matchesItem(slot: Int, item: Any?, exactly: Boolean): Boolean {
        requireSupportedItem(item)
        val target: ItemStack = inventory.getItemStack(slot) ?: return item == null
        if (item is ItemStack) return if (exactly) target == item else target.isSimilar(item as ItemStack)

        return false
    }

    override fun isSupportedItem(item: Any?): Boolean {
        return item == null || item is ItemStack
    }

    private fun requireSupportedItem(item: Any?) {
        if (isSupportedItem(item)) return

        throw IllegalStateException(
            "Unsupported item type: " + item!!.javaClass.name
        )
    }

    override fun hasItem(slot: Int): Boolean {
        return !inventory.getItemStack(slot).isAir
    }

    override fun getSize(): Int {
        return inventory.size
    }

    override fun getSlotsCount(): Int {
        return size - 1
    }

    override fun getFirstSlot(): Int {
        return 0
    }

    override fun getLastSlot(): Int {
        val resultSlots = getType().resultSlots
        var lastSlot = slotsCount
        if (resultSlots != null) {
            for (resultSlot in resultSlots) {
                if (resultSlot == lastSlot) lastSlot--
            }
        }

        return lastSlot
    }

    override fun changeTitle(title: String?, target: Viewer) {
        changeTitle(title?.let { Component.text(it) } ?: Component.empty(), (target as MinestomViewer).player)
    }

    fun changeTitle(title: Component, target: Player) {
        val open: Inventory = target.openInventory as? Inventory ?: return
        if (inventory.inventoryType == InventoryType.CRAFTING || inventory.inventoryType == InventoryType.CRAFTER_3X3) return
        open.setTitle(title)
    }

    override fun isEntityContainer(): Boolean {
        return inventory is PlayerInventory
    }

    override fun open(viewer: Viewer) {
        viewer.open(this)
    }

    override fun close() {
        inventory.viewers.forEach(Player::closeInventory)
    }

    override fun close(viewer: Viewer) {
        viewer.close()
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as MinestomViewContainer
        return isShared == that.isShared && inventory == that.inventory
                && getType() == that.getType()
    }

    override fun hashCode(): Int {
        return Objects.hash(inventory, isShared, getType())
    }

    override fun toString(): String {
        return "BukkitViewContainer{" + "inventory=" + inventory + ", shared=" + isShared + ", type=" + type + '}'
    }
}
