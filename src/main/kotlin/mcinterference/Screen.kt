package mcinterference

import space.kscience.kmath.geometry.Vector3D

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
    private val position: Vector3D,
    private val dx: Double,
    private val dy: Double,
    private val nx: Int,
    private val ny: Int
) {
    /**
     * A function for calculating amplitude at every point of the screen
     *
     * @param[accuracy] Desired number of calculated points for MC integration
     * @return Matrix of amplitudes
     */
    suspend fun draw(accuracy: Int) : List<List<Double>> =  List(nx) { rnum: Int ->
        List(ny) {
            fresnelIntegral(source as ContinuousEmitter, Vector3D(position.x + rnum * dx, position.y + it * dy, position.z), accuracy).re
        }
    }
}