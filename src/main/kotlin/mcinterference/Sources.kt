package mcinterference

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
    val amplitude: (DoubleVector2D) -> Complex,
    override val sampler: MeasuredSampler<DoubleVector2D>,
    val generator: RandomGenerator
): ContinuousEmitter {
    override var cache: MutableList<Wave>? = null
    override var cachedAccuracy = 0

    override suspend fun compute(accuracy: Int) {
        if (cache == null)
            cache = mutableListOf()
        val newPoints: Chain<DoubleVector2D> = sampler.sample(generator)
        for (i in (0..accuracy - cachedAccuracy)) {
            val point = newPoints.next()
            cache?.add(Wave(
                Euclidean3DSpace.vector(
                    point.x,
                    point.y,
                    0.0),
                amplitude(point)))
        }
        cachedAccuracy = accuracy
    }
}