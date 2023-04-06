package mcinterference

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.SimpleChain
import space.kscience.kmath.geometry.Vector2D
import space.kscience.kmath.stat.RandomGenerator
import space.kscience.kmath.stat.Sampler
import kotlin.math.cos
import kotlin.math.sin

/**
 * Interface with [measure] of the sampling area for integration purposes
 *
 * @property[measure] Measure of the area the points are sampled from
 */
interface MeasuredSampler<out T: Any>: Sampler<T> {
    val measure: Double
}

/**
 * A rectangle sampler
 *
 * @property[dx] Horizontal size of the rectangle
 * @property[dy] Vertical size of the rectangle
 * @property[offset] Offset of the central point of the rectangle
 * @property[rotation] Rotation angle of the rectangle
 */
class RectangleSampler(
    private val dx: Double,
    private val dy: Double,
    val offset: Vector2D = Vector2D(.0, .0),
    val rotation: Double = .0
) : MeasuredSampler<Vector2D> {
    override val measure: Double
        get() = dx * dy

    override fun sample(generator: RandomGenerator): Chain<Vector2D> {
        return SimpleChain {
            val x = offset.x + (generator.nextDouble() - 0.5) * dx
            val y = offset.y + (generator.nextDouble() - 0.5) * dy
            Vector2D(x * cos(rotation) + y * sin(rotation), y * cos(rotation) - x * sin(rotation))
        }
    }
}