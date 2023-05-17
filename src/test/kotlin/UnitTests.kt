
import kotlinx.coroutines.runBlocking
import mcinterference.*
import org.junit.jupiter.api.Test
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.Euclidean3DSpace
import kotlin.math.PI
import kotlin.test.assertEquals

internal class UnitTests {

    @Test
    fun waveTest() = Integration().run {
            val wave1 = Wave(Euclidean3DSpace.vector(.0, .0, .0), Complex(720))
            val wave2 = Wave(Euclidean3DSpace.vector(.0, 720.0, 720.0), Complex(720).rotate(PI / 2))
            val point = Euclidean3DSpace.vector(.0, .0, 720.0)
            assertEquals(wave1.amplitude(point).re, 1.0, 1e-4)
            assertEquals(wave2.amplitude(point).im, 1.0, 1e-4)
        }

    @Test
    fun pointSourceTest() {
        val pointSource = PointSource(Wave(Euclidean3DSpace.vector(.0, .0, .0), Complex(720)))
        val screen = Screen(pointSource, Euclidean3DSpace.vector(-1.0, -1.0, 720), .01, .01, 200, 200)
        val data = runBlocking { screen.draw(1, Integration()) }
        assertEquals(data[100][100], 1.0, 1e-3)
    }

    @Test
    fun imageTest() {
        val data = List(800) {
            List(600) {
                (it % 2).toDouble()
            }
        }
        createPng(data, "test")
    }
}