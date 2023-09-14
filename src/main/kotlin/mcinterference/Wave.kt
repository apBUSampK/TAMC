package mcinterference

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.take
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.geometry.DoubleVector3D
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.operations.invoke
import kotlin.coroutines.coroutineContext
import kotlin.math.PI


/**
 * Representation of a spherical wave
 *
 * @property[coordinate] Location of the source point
 * @property[stAmplitude] Initial amplitude of the wave
 */
data class Wave (
    val coordinate: DoubleVector3D,
    val stAmplitude: Complex
)

/**
 * Context class, encapsulating all arithmetic needed for calculations
 *
 * @property[wavelength] Wavelength of waves used in calculations
 * @property[maxCache] maximum cache (number of points) for each continuous object
 */
class FresnelIntegration (
    private val wavelength: Double = 720E-9,
    val maxCache: Int = 1_000_000_000,
    private val threads: Int = 1
) {
    init{
        if (threads < 1) throw IllegalArgumentException("Can't have less than one thread!")
    }
    /**
     * A function to rotate the complex number
     *
     * @param[alpha] Angle of rotation
     * @return [this] rotated by [alpha] counter-clockwise
     */
    fun Complex.rotate(alpha: Double) : Complex  = ComplexField {
        this@rotate * exp(i * alpha)
    }
    
    /**
     * Calculate wave's amplitude at a given point
     *
     * @property[target] Coordinate of the target point
     * @return Amplitude at the point
     */
    fun Wave.amplitude(target: DoubleVector3D): Complex = Euclidean3DSpace {
        val distance: Double = (target - coordinate).norm()
        ComplexField { stAmplitude / distance }.rotate(2 * PI * distance / wavelength)
    }

    /**
     * Calculate wave's amplitude multiplied by an inclination factor
     *
     * @property[target] Coordinate of the target point
     * @return Amplitude at the point times inclination factor
     */
    fun Wave.fresnel(target: DoubleVector3D): Complex = ComplexField {
        amplitude(target) * Euclidean3DSpace {
            val delta = (target - coordinate)
            1 + delta.dot(vector(.0, .0, 1.0)) / delta.norm() / 2
        }
    }

    /**
     * Fresnel MC integral function for a continuous emitter source
     *
     * @param[source] The source of the wavefront
     * @param[position] Position of the target point
     * @param[accuracy] Desired number of points for MC integration (redundant for PointEmitter source)
     * @return Complex amplitude of the E field at [position]
     */
    suspend fun fresnelIntegral(source: Emitter, position: DoubleVector3D, accuracy: Int = 1) : Complex = when(source){
        is ContinuousEmitter -> ComplexField {
            val batchSize = accuracy / threads
            val lastBatch = batchSize + accuracy - batchSize * threads
            val masterFlow = source.waves.take(accuracy)
            val flows = List(threads) { thread ->
                masterFlow.drop(thread * batchSize)
                    .take(if (thread != threads - 1) batchSize else lastBatch)
                    .map { it.fresnel(position) }
            }
            val data = flows.map {
                    CoroutineScope(coroutineContext).async {
                        it.reduce { a, b -> a + b } }
            }
            return data.awaitAll().reduce{ a, b -> a + b } / accuracy / source.sampler.density / i / wavelength
        }
        is PointEmitter -> ComplexField {
            source.wave.amplitude(position)
        }
    }
}