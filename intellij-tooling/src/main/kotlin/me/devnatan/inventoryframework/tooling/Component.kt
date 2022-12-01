package me.devnatan.inventoryframework.tooling

import com.intellij.util.ImageLoader
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagLayout
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.swing.JComponent
import javax.swing.JPanel

class ViewComponent(val declaration: ViewDeclaration) : JPanel() {

    private val scale = declaration.type.scale.toDouble()
    private val sprite = loadSprite()

    init {
        isVisible = true
        minimumSize = dimensionOf(declaration.type, declaration.lines)
    }

    private fun loadSprite(): BufferedImage {
        val sprite = sprite("chest-%s.png".format(declaration.lines))
        println("Reading sprite $sprite")
        val image = ImageLoader.loadFromResource(sprite, this::class.java)!!
        println("Loaded image: $image")
        return toBufferedImage(image)
    }

    override fun paintComponent(g: Graphics) {
        g as Graphics2D
        g.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g.setRenderingHint(
            RenderingHints.KEY_RENDERING,
            RenderingHints.VALUE_RENDER_QUALITY
        );
        g.setRenderingHint(
            RenderingHints.KEY_STROKE_CONTROL,
            RenderingHints.VALUE_STROKE_PURE
        );
        g.scale(scale, scale)
        g.drawImage(sprite, 0, 0, this)
    }

}

fun JComponent.centered() = JPanel().apply {
    isVisible = true
    layout = GridBagLayout()
    add(this@centered)
}