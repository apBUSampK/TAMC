package mcinterference

import space.kscience.kmath.chains.Chain
import space.kscience.kmath.chains.SimpleChain
import space.kscience.kmath.geometry.DoubleVector2D
import space.kscience.kmath.geometry.Euclidean2DSpace
import space.kscience.kmath.random.RandomGenerator
import space.kscience.kmath.stat.Sampler
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Interface with [density] of the sampling area for integration purposes
 *
 * @property[density] Measure of the area the points are sampled from
 */
interface MeasuredSampler<out T: Any>: Sampler<T> {
    val density: Double
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
    private val offset: DoubleVector2D = Euclidean2DSpace.vector(.0, .0),
    private val rotation: Double = .0
) : MeasuredSampler<DoubleVector2D> {
    override val density: Double
        get() = 1 / dx / dy

    override fun sample(generator: RandomGenerator): Chain<DoubleVector2D> {
        return SimpleChain {
            val x = offset.x + (generator.nextDouble() - 0.5) * dx
            val y = offset.y + (generator.nextDouble() - 0.5) * dy
            Euclidean2DSpace.vector(x * cos(rotation) + y * sin(rotation), y * cos(rotation) - x * sin(rotation))
        }
    }
}

/**
 * A circle sampler
 *
 * @property[R] Radius of the circle
 * @property[offset] Offset of the central point of the rectangle
 */
class CircleSampler(
    private val r: Double,
    private val offset: DoubleVector2D = Euclidean2DSpace.vector(.0, .0)
) : MeasuredSampler<DoubleVector2D> {
    override val density: Double
        get() = 1 / PI / r / r

    override fun sample(generator: RandomGenerator): Chain<DoubleVector2D> {
        return SimpleChain {
            Euclidean2DSpace.run {
                val phi = generator.nextDouble() * 2 * PI
                val rGen = r * sqrt(generator.nextDouble())
                offset + vector(rGen * cos(phi), rGen * sin(phi))
            }
        }
    }
}