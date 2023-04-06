package mcinterference

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.geometry.Vector3D
import space.kscience.kmath.stat.RandomGenerator

/**
 * A continuous source
 *
 * @property[amplitude] A function for determining the amplitude at a given point
 * @property[sampler] A sampler for generating new points. Should be chosen accordingly to [amplitude]
 * @property[generator] A seeded generator for the [sampler]
 */
class ContinuousSource (
    val amplitude: (Vector2D) -> Complex,
    override val sampler: MeasuredSampler<Vector2D>,
    val generator: RandomGenerator
): ContinuousEmitter {
    override var cache: MutableList<Wave>? = null
    override var cachedAccuracy = 0

    override suspend fun compute(accuracy: Int) {
        if (cache == null)
            cache = mutableListOf()
        val newPoints: Chain<Vector2D> = sampler.sample(generator)
        for (i in (0..accuracy - cachedAccuracy)) {
            val point = newPoints.next()
            cache?.add(Wave(
                Vector3D(
                    point.x,
                    point.y,
                    0.0),
                amplitude(point)))
        }
        cachedAccuracy = accuracy
    }
}