package me.devnatan.inventoryframework

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import java.util.Objects

class MinestomViewContainer(
    private val inventory: Inventory,
    shared: Boolean,
    private val type: ViewType,
    private val proxied: Boolean,
) : ViewContainer {
    val isShared: Boolean = shared

    fun getInventory(): Inventory = inventory

    override fun isProxied(): Boolean = proxied

    override fun getTitle(): String {
        val diffTitle: Boolean =
            inventory.viewers
                .mapNotNull { it.openInventory as? Inventory }
                .map { PlainTextComponentSerializer.plainText().serialize(it.title) }
                .distinct()
                .any()

        check(!(diffTitle && isShared)) { "Cannot get unique title of shared inventory" }
        val openInventory = inventory.viewers.first().openInventory
        return (openInventory as? Inventory)?.title?.let {
            PlainTextComponentSerializer.plainText().serialize(it)
        } ?: ""
    }

    override fun getTitle(viewer: Viewer): String =
        ((viewer as MinestomViewer).player.openInventory as? Inventory)?.title?.let {
            PlainTextComponentSerializer.plainText().serialize(it)
        } ?: ""

    override fun getType(): ViewType = type

    override fun getRowsCount(): Int = size / columnsCount

    override fun getColumnsCount(): Int = type.columns

    override fun renderItem(
        slot: Int,
        item: Any,
    ) {
        requireSupportedItem(item)
        inventory.setItemStack(slot, item as ItemStack)
    }

    override fun removeItem(slot: Int) {
        inventory.setItemStack(slot, ItemStack.AIR)
    }

    override fun matchesItem(
        slot: Int,
        item: Any?,
        exactly: Boolean,
    ): Boolean {
        requireSupportedItem(item)
        val target: ItemStack = inventory.getItemStack(slot)
        if (item is ItemStack) {
            return if (exactly) {
                target == item
            } else {
                target.isSimilar(item)
            }
        }

        return false
    }

    override fun isSupportedItem(item: Any?): Boolean = item == null || item is ItemStack

    private fun requireSupportedItem(item: Any?) {
        if (isSupportedItem(item)) return

        throw IllegalStateException("Unsupported item type: " + item!!.javaClass.name)
    }

    override fun hasItem(slot: Int): Boolean = !inventory.getItemStack(slot).isAir

    override fun getSize(): Int = inventory.size

    override fun getSlotsCount(): Int = size - 1

    override fun getFirstSlot(): Int = 0

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

    override fun changeTitle(
        title: String?,
        target: Viewer,
    ) {
        changeTitle(
            title?.let { Component.text(it) } ?: Component.empty(),
            (target as MinestomViewer).player,
        )
    }

    fun changeTitle(
        title: Component,
        target: Player,
    ) {
        val open: Inventory = target.openInventory as? Inventory ?: return
        if (
            inventory.inventoryType == InventoryType.CRAFTING ||
            inventory.inventoryType == InventoryType.CRAFTER_3X3
        ) {
            return
        }
        open.title = title
    }

    override fun isEntityContainer(): Boolean {
        // Cannot be an entity container
        return false
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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as MinestomViewContainer
        return isShared == that.isShared &&
            inventory == that.inventory &&
            getType() == that.getType()
    }

    override fun hashCode(): Int = Objects.hash(inventory, isShared, getType())

    override fun toString(): String = "BukkitViewContainer{inventory=$inventory, shared=$isShared, type=$type}"
}
