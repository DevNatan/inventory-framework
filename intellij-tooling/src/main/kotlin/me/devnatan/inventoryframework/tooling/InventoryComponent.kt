package br.com.devsrsouza.kotlinbukkitapi.tooling.menu

import com.intellij.util.ImageLoader
import org.jetbrains.kotlin.utils.keysToMap
import java.awt.*
import javax.swing.JPanel

class InventoryComponent(
    val declaration: MenuDeclaration
) : JPanel() {
    companion object {
        val CHEST_SCALE = 2
        val CHEST_SINGLE_LINE_HEIGHT = 42
        val CHEST_LINE_DIFF = 18
        val CHEST_WIDTH = 176

        private val minecraftFontResource = "/assets/fonts/Minecraftia-Regular.ttf"
        private val spritesFolder = "/assets/sprites"
        private val itemsSpriteFolder = "/assets/items"
        private val items13SpriteFolder = "/assets/items-13"

        // the selected-border is 36x36 and not 32x32
        private val selectedBorderSizeDiff = 2
    }

    private val lines = when {
        declaration.lines > 6 -> 6
        declaration.lines < 1 -> 1
        else -> declaration.lines
    }

    private val spriteImage = ImageLoader.loadFromResource(
        "$spritesFolder/chest-${lines}.png"
    )!!
    private val itemsImage = declaration.slots.filter {
        it.line in 1..declaration.lines && it.slot in 1..9
    }.keysToMap {
        // TODO: Support data
        val item13 = runCatching { MinecraftItem13.valueOf(it.item) }.getOrNull()

        if(item13 != null) {
            ImageLoader.loadFromResource("$items13SpriteFolder/${item13.name.toLowerCase()}.png")
        } else {
            val item = runCatching { MinecraftItem.valueOf(it.item) }.getOrNull()

            if (item != null) {
                ImageLoader.loadFromResource("$itemsSpriteFolder/${item.id}-0.png")
            } else {
                null
            }
        }
    }
    private val selectionImage = if(declaration.slots.any { it.isSelected })
        ImageLoader.loadFromResource("$spritesFolder/selected-border.png")
    else null

    private val minecraftFont = Font.createFont(
        Font.TRUETYPE_FONT,
        this::class.java.getResource(minecraftFontResource).openStream()
    ).deriveFont(18.5f)

    init {
        isVisible = true
        minimumSize = calculateDimension()
    }
    override fun paintComponent(g: Graphics) {
        val g = g as Graphics2D
        val oldTransform = g.transform

        // drawing menu
        g.scale(CHEST_SCALE.toDouble(), CHEST_SCALE.toDouble())
        g.drawImage(spriteImage, 0, 0,this)

        g.transform = oldTransform

        // drawing items
        for ((slot, texture) in itemsImage) {
            val (x, y) = calculateXYForSlot(slot)

            g.drawImage(texture ?: continue, x, y, this)

            // if is selected draw selection border
            if(slot.isSelected) {
                g.drawImage(
                    selectionImage ?: continue,
                    x- selectedBorderSizeDiff,
                    y- selectedBorderSizeDiff,
                    this
                )
            }
        }

        // drawing displayname
        g.font = minecraftFont
        g.color = Color.DARK_GRAY
        g.drawString(declaration.displayname, 16, 40)
    }

    fun calculateXYForSlot(menuSlotDeclaration: MenuSlotDeclaration): Pair<Int, Int> {
        val topXPadding = 18 * CHEST_SCALE
        val leftYPadding = 8 * CHEST_SCALE
        val itemSize = 16 * CHEST_SCALE
        val rightYPadding = 2 * CHEST_SCALE
        val bottomXPadding = 2 * CHEST_SCALE

        val itemXPos = menuSlotDeclaration.line -1
        val itemYPos = menuSlotDeclaration.slot -1

        val x = leftYPadding + (itemYPos * rightYPadding) + (itemYPos * itemSize)
        val y = topXPadding + (itemXPos * bottomXPadding) + (itemXPos * itemSize)

        return x to y
    }

    private fun calculateDimension(): Dimension {
        val height = CHEST_SINGLE_LINE_HEIGHT + (lines - 1) * CHEST_LINE_DIFF

        return Dimension((CHEST_WIDTH * CHEST_SCALE).toInt(), (height * CHEST_SCALE).toInt())
    }
}