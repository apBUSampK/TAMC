package mcinterference

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator

/**
 * A continuous source
 *
 * @property[amplitude] A function for determining the amplitude at a given point
 * @property[sampler] A sampler for generating new points. Should be chosen accordingly to [amplitude]
 * @property[generator] A seeded generator for the [sampler]
 */
class ContinuousSource (
    private val amplitude: (DoubleVector2D) -> Complex,
    override val sampler: MeasuredSampler<DoubleVector2D>,
    private val generator: RandomGenerator
): ContinuousEmitter {
    private var cache: MutableList<Wave> = mutableListOf()

    fun compute(accuracy: Int) {
        val newPoints: Chain<DoubleVector2D> = sampler.sample(generator)
        for (i in (0..accuracy - cache.size)) {
            val point = runBlocking {  newPoints.next() }                  //suppress asynchronous behaviour
            cache.add(Wave(
                Euclidean3DSpace.vector(
                    point.x,
                    point.y,
                    0.0),
                amplitude(point)))
        }
    }

    override fun emit(accuracy: Int, context: FresnelIntegration): List<Wave> {
        if (cache.size < accuracy)
            compute(accuracy)               //context is redundant now but can be expanded in future
        return cache.subList(0, accuracy)
    }
}