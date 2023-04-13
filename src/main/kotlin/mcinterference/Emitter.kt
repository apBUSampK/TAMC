package mcinterference

import space.kscience.kmath.geometry.DoubleVector2D

/**
 * Emitter interface, anything that is considered a light source
 */
sealed interface Emitter

/**
 * Continuous emitter, any emitter with geometrical size
 */
interface ContinuousEmitter : Emitter {

    val sampler: MeasuredSampler<DoubleVector2D>

    /**
     * Emit function: generate a list of spherical waves, located at random points of the emitter.
     *
     * @param[accuracy] Number of generated points
     * @return List of [accuracy] spherical waves
     */
    fun emit(accuracy: Int, context:FresnelIntegration): List<Wave>
}

/**
 * Point emitter
 *
 * @property[wave] The emitted spherical wave
 */
interface PointEmitter : Emitter {
    val wave: Wave
}