package mcinterference

import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun createPng(data: List<List<Double>>, name: String, h: Float = 0F, s: Float = 0F) : Unit {
    val width = data.size
    val height = data[0].size
    val pixels = data.flatten()
    val norm = pixels.max()
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    image.setRGB(0, 0, width, height, pixels.map { Color.getHSBColor(h, s, (it / norm).toFloat()).rgb }.toIntArray(), 0, width)
    val fileOut = File("$name.png")
    ImageIO.write(image, "png", fileOut)
}