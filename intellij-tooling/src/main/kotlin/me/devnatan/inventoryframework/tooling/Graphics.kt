package me.devnatan.inventoryframework.tooling

import java.awt.Dimension
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage

fun dimensionOf(type: ViewType, lines: Int): Dimension {
    val height = type.singleLineHeight + (lines - 1) * type.lineDiff
    return Dimension((type.width * type.scale), (height * type.scale))
}

fun toBufferedImage(img: Image): BufferedImage {
    if (img is BufferedImage) {
        return img
    }

    // Create a buffered image with transparency
    val bimage = BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB)

    // Draw the image on to the buffered image
    val bGr: Graphics2D = bimage.createGraphics()
    bGr.drawImage(img, 0, 0, null)
    bGr.dispose()

    // Return the buffered image
    return bimage
}