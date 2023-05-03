package mcinterference

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.random.RandomGenerator

/**
 * A continuous source
 *
 * @property[amplitude] A function for determining the amplitude at a given point
 * @property[sampler] A sampler for generating new points. Should be chosen accordingly to [amplitude]
 * @param[generator] A seeded generator for the [sampler]
 */
class ContinuousSource (
    private val amplitude: (DoubleVector2D) -> Complex,
    override val sampler: MeasuredSampler<DoubleVector2D>,
    generator: RandomGenerator,
    context: FresnelIntegration
): ContinuousEmitter {
    private val _waves = MutableSharedFlow<Wave>(context.maxCache, onBufferOverflow = BufferOverflow.DROP_LATEST)
    override val waves = _waves.asSharedFlow()
    private val newPoints = sampler.sample(generator)
    /**
     * A function for creating a Wave at a given point of the source
     * @param[point] A point on the source
     */
    fun getWave(point: DoubleVector2D): Wave =
        Wave(Euclidean3DSpace.vector(
                point.x,
                point.y,
                0),
            amplitude(point))

    override suspend fun request(accuracy: Int) {
        for (i in 0 until accuracy - _waves.replayCache.size)
            _waves.emit(getWave(newPoints.next()))
    }
}


/**
 * A point source
 *
 * @property[wave] A wave object, representing the point source
 */
class Source(
    override val wave: Wave
): PointEmitter