package mcinterference

import space.kscience.kmath.geometry.DoubleVector2D
import java.util.random.RandomGenerator

/**
 * This is a class for generating a modulation function of a randomised SLM. Intended usage is passing initialised
 * instance's [modulation] to the constructor of SLM.
 *
 *
 */
class RandomCTr(
    private val sampler: MeasuredSampler<DoubleVector2D>,
    private val generator: RandomGenerator,
    private val rC: Double,
    private val drC: Double,
    private val count: Int,
) {
    private val circles: List<Pair<DoubleVector2D, Double>> = buildList {
        for (i in 0 until count) {
            val success = false
            while (!success) {

            }
        }
    }
}