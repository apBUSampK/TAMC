package mcinterference

import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.geometry.DoubleVector3D
import space.kscience.kmath.geometry.Euclidean3DSpace

/**
 * A class for determining a grid on the screen for further rendering
 *
 * @property[source] The emitter which light this SLM is transforming
 * @property[position] Position of [1, 1] pixel relative to the [source]
 * @property[dx] Horizontal distance between pixels
 * @property[dy] Vertical distance between pixels
 * @property[nx] Horizontal size
 * @property[ny] Vertical size
 */
class Screen (
    private val source: Emitter,
    private val position: DoubleVector3D,
    private val dx: Double,
    private val dy: Double,
    private val nx: Int,
    private val ny: Int
) {
    /**
     * A function for calculating amplitude at every point of the screene
     *
     * @param[accuracy] Desired number of calculated points for MC integration
     * @return Matrix of amplitudes
     */
    suspend fun draw(accuracy: Int, context: Integration): List<List<Double>> = ComplexField.run {
        when(source) {
            is ContinuousEmitter -> source.request(accuracy)
            is PointEmitter -> Unit
        }
        List(nx) { rNum: Int ->
            List(ny) {
                power(
                    norm(context.integral(
                        source,
                        Euclidean3DSpace.vector(position.x + rNum * dx, position.y + it * dy, position.z),
                        accuracy
                    )), 2
                ).re
            }
        }
    }
}
