package mcinterference

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.random.RandomGenerator

/**
 * This is a class for generating a modulation function of a randomised SLM. Intended usage is passing initialised
 * instance's [modulation] to the constructor of SLM.
 *
 *
 */
class RandomCTr(
    private val rC: Double,
    private val drC: Double,
    private val count: Int,
    sampler: MeasuredSampler<DoubleVector2D>,
    private val generator: RandomGenerator
) {
    private val sample = sampler.sample(generator)
    private val circles: List<Pair<DoubleVector2D, Double>> = runBlocking {
        Euclidean2DSpace.run {
            buildList {
                for (i in 0 until count) {
                    do {
                        var success = true
                        val circle = Pair(sample.next(), rC + drC * (generator.nextDouble() - .5))
                        for (oldCircle in this)
                            if (norm(oldCircle.first - circle.first) < oldCircle.second + circle.second) {
                                success = false
                                break
                            }
                        if (success)
                            add(circle)
                    } while(!success)
                }
            }
        }
    }

    fun modulation(point: DoubleVector2D) : Complex = Euclidean2DSpace.run {
        for (circle in circles)
            if (norm(point - circle.first) < circle.second)
                return Complex(0)
        return Complex(1)
    }
}