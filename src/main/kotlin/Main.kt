
import mcinterference.*
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
    val width = 100
    val height = 100
    val source = ContinuousSource({ Complex(100) }, RectangleSampler(100.0, 100.0), RandomGenerator.default(0))
    val slm = SLM(source, 10.0, RectangleSampler(.1, 10.0), { Complex(1) }, RandomGenerator.default(1))
    val screen = Screen(slm, Euclidean3DSpace.vector(-5.0, -5.0, 5.0), .01, .01, width, height)
    val data = screen.draw(100000, FresnelIntegration(720E-7))
    val pixels = data.flatten()
    val norm = pixels.max()
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    image.setRGB(0, 0, width, height, pixels.map { Color.getHSBColor(0.167F, 0.4F, (it / norm).toFloat()).rgb }.toIntArray(), 0, width)
    val fileOut = File("image.png")
    ImageIO.write(image, "png", fileOut)
}