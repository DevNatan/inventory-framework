package me.devnatan.inventoryframework.tooling.preview

import com.intellij.ui.JBColor
import com.intellij.util.ImageLoader
import com.intellij.util.ui.ImageUtil
import me.devnatan.inventoryframework.tooling.model.IFDeclaration
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagLayout
import java.awt.Image
import java.awt.image.BufferedImage
import javax.swing.JPanel

class ViewComponent(private val declaration: IFDeclaration) : JPanel() {

    companion object {
        const val CHEST_SCALE = 2
        const val CHEST_SINGLE_LINE_HEIGHT = 42
        const val CHEST_LINE_DIFF = 18
        const val CHEST_WIDTH = 176
    }

    private val image: Image

    init {
        layout = GridBagLayout()
        image = ImageLoader.loadFromResource("/assets/sprites/chest-${declaration.lines}.png", this::class.java)!!
        isVisible = true
        minimumSize = calculateDimension()
    }

    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)

        println("paint")
        @Suppress("NAME_SHADOWING")
        val g = g as Graphics2D
        val oldTransform = g.transform
        g.scale(CHEST_SCALE.toDouble(), CHEST_SCALE.toDouble())
        g.drawImage(image, 0, 0, this)
        g.transform = oldTransform
    }

    private fun calculateDimension(): Dimension {
        val height = CHEST_SINGLE_LINE_HEIGHT + (declaration.lines - 1) * CHEST_LINE_DIFF
        return Dimension((CHEST_WIDTH * CHEST_SCALE), (height * CHEST_SCALE))
    }

}