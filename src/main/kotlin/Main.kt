
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
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
        val context = FresnelIntegration(720E-9)
        val source = Source(Wave(Euclidean3DSpace.vector(.0, .0, .0), Complex(100)))
        val slm = SLM(source, 10.0, RectangleSampler(.00002, .00002), { Complex(1) }, RandomGenerator.default(1), context)
        val screen = Screen(slm, Euclidean3DSpace.vector(-.5, -.5, 5), .01, .01, width, height)
        val data = runBlocking(Dispatchers.Default) {
            screen.draw(10000, context)
        }
        //image processing
        val pixels = data.flatten()
        val norm = pixels.max()
        print(norm)
        val image = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        image.setRGB(0, 0, width, height, pixels.map { Color.getHSBColor(0F, 0F, (it / norm).toFloat()).rgb }.toIntArray(), 0, width)
        val fileOut = File("image.png")
        ImageIO.write(image, "png", fileOut)
}