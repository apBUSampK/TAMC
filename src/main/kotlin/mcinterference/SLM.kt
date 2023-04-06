package mcinterference

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.geometry.Vector3D
import space.kscience.kmath.stat.RandomGenerator

/**
 * A class representing a SLM
 *
 * @property[source] The emitter which light this SLM is transforming
 * @property[distance] Distance to the source
 * @property[sampler] A sampler for generating new points. Should be chosen accordingly to [modulation]
 * @property[modulation] Function applied to incoming light
 * @property[generator] A seeded generator for the [sampler]
 */
class SLM(
    private val source: Emitter,
    private val distance: Double,
    override val sampler: MeasuredSampler<Vector2D>,
    val modulation: (Vector2D) -> Complex,
    private val generator: RandomGenerator
) : ContinuousEmitter {
    override var cache: MutableList<Wave>? = null
    override var cachedAccuracy = 0
    override suspend fun compute(accuracy: Int) {
        if (cache == null)
            cache = mutableListOf()
        val newPoints: Chain<Vector2D> = sampler.sample(generator)
        for (i in (0..accuracy - cachedAccuracy)) {
            val point = newPoints.next()
            val position = Vector3D(point.x, point.y, distance)
            cache?.add(Wave(
                Vector3D(
                    point.x,
                    point.y,
                    0.0),
                ComplexField.run{ modulation(point) * fresnelIntegral(source as ContinuousEmitter, position, accuracy) }))// TODO(type check for PointEmitter)
        }
        cachedAccuracy = accuracy
    }
}