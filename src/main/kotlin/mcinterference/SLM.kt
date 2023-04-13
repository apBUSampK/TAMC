package mcinterference

import kotlinx.coroutines.runBlocking
import space.kscience.kmath.chains.Chain
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.operations.invoke
import space.kscience.kmath.random.RandomGenerator

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
    override val sampler: MeasuredSampler<DoubleVector2D>,
    private val modulation: (DoubleVector2D) -> Complex,
    private val generator: RandomGenerator
) : ContinuousEmitter {
    private var cache: MutableList<Wave> = mutableListOf()

    fun compute(accuracy: Int, context: FresnelIntegration) {
        val newPoints: Chain<DoubleVector2D> = sampler.sample(generator)
        for (i in (0..accuracy - cache.size)) {
            val point = runBlocking { newPoints.next() }                        //suppress asynchronous behaviour
            val position = Euclidean3DSpace.vector(point.x, point.y, distance)
            cache.add(Wave(
                Euclidean3DSpace.vector(
                    point.x,
                    point.y,
                    0.0),
                ComplexField { modulation(point) * context.fresnelIntegral(source , position, accuracy) }))
        }
    }
    override fun emit(accuracy: Int, context: FresnelIntegration): List<Wave> {
        if (cache.size < accuracy)
            compute(accuracy, context)
        return cache.subList(0, accuracy)
    }
}