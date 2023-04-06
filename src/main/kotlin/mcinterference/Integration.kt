package mcinterference

import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.geometry.Vector3D

/**
 * Frenel MC integral function for a continuous emitter source
 *
 * @param[source] The source of the wavefront
 * @param[position] Position of the target point
 * @param[accuracy] Desired number of points for MC integration
 * @return Complex amplitude of the E field at [position]
 */
suspend fun fresnelIntegral(source: ContinuousEmitter, position: Vector3D, accuracy: Int) : Complex = ComplexField.run{
    source.sampler.measure * accuracy * source.emit(accuracy)
        .map { it.fresnel(position) }
        .reduce{ a, b -> a + b }
}

fun fresnelIntegral(source: PointEmitter, position: Vector3D) : Complex = ComplexField.run{
    TODO()
}