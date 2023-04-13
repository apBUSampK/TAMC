package mcinterference

import space.kscience.kmath.geometry.DoubleVector2D

/**
 * Emitter interface, anything that is considered a light source
 */
sealed interface Emitter {
    /**
     * Emit function: generate a list of spherical waves, located at random points of the emitter.
     *
     * @param[accuracy] Number of generated points
     * @return List of [accuracy] spherical waves
     */
    suspend fun emit(accuracy: Int) : List<Wave>
}

/**
 * Continuous emitter, any emitter with geometrical size
 */
interface ContinuousEmitter : Emitter {
    val sampler: MeasuredSampler<DoubleVector2D>
    val cache: List<Wave>?
    val cachedAccuracy: Int

    /**
     * Function to add new points to cache
     *
     * @param[accuracy] Desired number of cached points
     */
    suspend fun compute(accuracy: Int) : Unit
    override suspend fun emit(accuracy: Int): List<Wave> {
        if (cachedAccuracy < accuracy) compute(accuracy)
        return cache!!
    }
}

interface PointEmitter : Emitter {
    //TODO(Manage some logic)
    //emit is actually redundant in this interface, maybe remove it from Emitter
}