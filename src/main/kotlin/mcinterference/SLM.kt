package mcinterference

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
 * @param[generator] A seeded generator for the [sampler]
 */
class SLM(
    private val source: Emitter,
    private val distance: Double,
    override val sampler: MeasuredSampler<DoubleVector2D>,
    private val modulation: (DoubleVector2D) -> Complex,
    generator: RandomGenerator,
    val context: FresnelIntegration
) : ContinuousEmitter {
    private val _waves = MutableSharedFlow<Wave>(replay=context.maxCache)
    override val waves = _waves.asSharedFlow()
    private val newPoints = sampler.sample(generator)

    suspend fun getWave(point: DoubleVector2D, accuracy: Int): Wave =
        Wave(Euclidean3DSpace.vector(
                point.x,
                point.y,
                0.0),
            ComplexField { modulation(point) * context.fresnelIntegral(source , Euclidean3DSpace.vector(point.x, point.y, distance), accuracy) })

    override suspend fun request(accuracy: Int) {
        for (i in (0 until accuracy - _waves.replayCache.size))
            _waves.emit(getWave(newPoints.next(), accuracy))
    }
}