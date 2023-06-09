package mcinterference

import kotlinx.coroutines.flow.SharedFlow
import space.kscience.kmath.geometry.DoubleVector2D

/**
 * Emitter interface, anything that is considered a light source
 */
sealed interface Emitter

/**
 * Continuous emitter, any emitter with geometrical size
 *
 * @property[sampler] A sampler with attached measure for sampling points
 * @property[waves] A hot flow of emitted waves (cache)
 */
interface ContinuousEmitter : Emitter {

    val sampler: MeasuredSampler<DoubleVector2D>
    val waves: SharedFlow<Wave>

    /**
     * Emit function: generate a list of spherical waves, located at random points of the emitter.
     *
     * @param[accuracy] Number of generated points
     * @return List of [accuracy] spherical waves
     */
    suspend fun request(accuracy: Int)
}

/**
 * Point emitter
 *
 * @property[wave] The emitted spherical wave
 */
interface PointEmitter : Emitter {
    val wave: Wave
}