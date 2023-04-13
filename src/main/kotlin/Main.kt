
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mcinterference.*
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator

fun main(args: Array<String>) {
    ModellingSettings.wavelength = 5e-4
    val source = ContinuousSource({ Complex(100) }, RectangleSampler(100.0, 100.0), RandomGenerator.default(0))
    val slm = SLM(source, 10.0, RectangleSampler(.1, 10.0), { Complex(1) }, RandomGenerator.default(1))
    val screen = Screen(slm, Euclidean3DSpace.vector(-5.0, -5.0, 5.0), .01, .01, 100, 100)
    runBlocking {
        launch {
            print(screen.draw(10000))
        }
    }
}