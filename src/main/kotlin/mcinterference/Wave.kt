package mcinterference

import mcinterference.ModellingSettings.wavelength
import space.kscience.kmath.complex.Complex
import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.geometry.Euclidean3DSpace
import space.kscience.kmath.geometry.Vector3D
import kotlin.math.PI

/**
 * A function to rotate the complex number
 *
 * @param[alpha] Angle of rotation
 * @return [this] rotated by [alpha] counter-clockwise
 */
fun Complex.rotate(alpha: Double) : Complex  = ComplexField.run{
    this@rotate * exp(i * alpha)
}

/**
 * An object resembling modelling settings for the whole setup
 *
 * @property[wavelength] Wavelength of the source
 */
//wavelength is used everywhere and it certainly isn't a property of, for example, an SLM. Polluting Wave with it, when it is the same for all the waves, felt strange too.
object ModellingSettings {
    var wavelength : Double = .0
}

/**
 * Representation of a spherical wave
 *
 * @property[coordinate] Location of the source point
 * @property[stAmplitude] Initial amplitude of the wave
 */
class Wave (
    private val coordinate: Vector3D,
    private val stAmplitude: Complex
) {
    /**
     * Calculate wave's amplitude at a given point
     *
     * @property[target] Coordinate of the target point
     * @return Amplitude at the point
     */
    fun amplitude(target: Vector3D) : Complex = Euclidean3DSpace.run{
        val distance: Double = (target - coordinate).norm()
        ComplexField.run{stAmplitude / distance}.rotate(2 * PI * distance / ModellingSettings.wavelength)
    }

    /**
     * Calculate wave's amplitude multiplied by an inclination factor
     *
     * @property[target] Coordinate of the target point
     * @return Amplitude at the point times inclinationn factor
     */
    fun fresnel(target: Vector3D): Complex = ComplexField.run{
         -i / ModellingSettings.wavelength * amplitude(target) * (1 + Euclidean3DSpace.run {
             val delta = (target - coordinate)
             delta.dot(Vector3D(.0, .0, 1.0)) / delta.norm() }) / 2}
}