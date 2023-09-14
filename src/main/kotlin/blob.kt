
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import mcinterference.*
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
        val width = 100
        val height = 100
        val context = FresnelIntegration(720E-9, threads = 4)
        val sampler = CircleSampler(8e-4)
        val rslm = SLM(
                null,
                10.0,
                sampler,
                { Complex(1) },//RandomCTr(7e-6, 5e-7, 90, sampler, RandomGenerator.default(1), true)::modulation,
                RandomGenerator.default(2),
                context
        )
        val screen = Screen(rslm, Euclidean3DSpace.vector(-.005, -.005, 4.0/9), .0001, .0001, width, height)
        println( measureTimeMillis {
                val data = runBlocking(Dispatchers.Default) {
                        screen.draw(10000, context)
                }
                createPng(data, "testfr2")
                createPng(data, "testfr2_tr", transform = { sqrt(it) })
        })
}