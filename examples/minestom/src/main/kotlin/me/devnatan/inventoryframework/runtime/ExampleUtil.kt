package me.devnatan.inventoryframework.runtime

import net.kyori.adventure.text.Component
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material
import java.util.*


object ExampleUtil {
    @JvmStatic
	fun getRandomItems(amount: Int): List<ItemStack> {
        val materials = Material.values().toTypedArray()
        val random = Random()

        val result = ArrayList<ItemStack>()

        for (i in 0 until amount) {
            result.add(ItemStack.of(materials[random.nextInt(10, 100)]))
        }

        return result
    }

    @JvmStatic
	fun displayItem(material: Material, displayName: String): ItemStack {
        return ItemStack.of(material).withCustomName(Component.text(displayName))
    }
}
