
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mcinterference.*
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator

fun main(args: Array<String>) {
        val width = 100
        val height = 100
        val context = Integration(720E-9)
        val pointSource = PointSource(Wave(Euclidean3DSpace.vector(.0, .0, .0), Complex(100)))
        val slm = SLM(pointSource, 10.0, RectangleSampler(.00002, .00002), { Complex(1) }, RandomGenerator.default(1), context)
        val screen = Screen(slm, Euclidean3DSpace.vector(-.5, -.5, 5), .01, .01, width, height)
        val data = runBlocking(Dispatchers.Default) {
            screen.draw(2, context)
        }
        createPng(data, "test")
}