
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mcinterference.*
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val width = 600
    val height = 600
    val context = FresnelIntegration(720E-9, threads = 4)
    val sampler = CircleSampler(8e-5)
    val rslm = SLM(
        null,
        10.0,
        sampler,
        RandomCTr(7e-6, 5e-7, 90, sampler, RandomGenerator.default(1), true)::modulation,
        RandomGenerator.default(2),
        context
    )
    val screen = Screen(rslm, Euclidean3DSpace.vector(-.3, -.3, 5), .001, .001, width, height)
    println( measureTimeMillis {
        val data = runBlocking(Dispatchers.Default) {
            screen.draw(10000, context)
        }
        createPng(data, "vpvrand90corr")
        createPng(data, "vpvrand90corr_tr", transform = {sqrt(it)})
    })
}