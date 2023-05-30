
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mcinterference.*
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val width = 100
    val height = 100
    val context = FresnelIntegration(720E-9, threads = 4)
    val sampler = CircleSampler(8e-5)
    val rslm = SLM(
        null,
        .0,
        sampler,
        RandomCTr(7e-6, 5e-7, 100, sampler, RandomGenerator.default(1))::modulation,
        RandomGenerator.default(2),
        context
    )
    val screen = Screen(rslm, Euclidean3DSpace.vector(-.5, -.5, 5), .01, .01, width, height)
    print( measureTimeMillis {
        val data = runBlocking(Dispatchers.Default) {
            screen.draw(100000, context)
        }
        createPng(data, "vpvlong")
    })
}