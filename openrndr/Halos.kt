import org.openrndr.application
import org.openrndr.color.ColorRGBa
import org.openrndr.extra.noise.simplex
import org.openrndr.extras.easing.*
import org.openrndr.ffmpeg.ScreenRecorder
import org.openrndr.math.Vector2
import org.openrndr.math.mix
import org.openrndr.shape.Circle
import kotlin.math.*

// by Mark-Felix

fun main() = application {
    configure { width = 800; height = 800 }
    program {
        val golden = 1.61803398875
        val totalFrames = 900
        val scale = 0.25
        val numBranches = 500
        val numGrains = 42
        val thickness = (width + height) * (width / height) * 0.0065

        // Uncomment to save as mp4
        //extend(ScreenRecorder())

        extend {
            drawer.translate(drawer.bounds.center)

            backgroundColor = ColorRGBa.BLACK
            drawer.stroke = ColorRGBa.WHITE.opacify(0.1)
            drawer.fill = ColorRGBa.YELLOW.opacify(0.02)

            val percent = (1.0 * frameCount % totalFrames) / totalFrames
            drawer.rotate(135.0 + 180.0 * easeQuintInOut(percent, 0.5, 1.0, 1.0))
            val percentRadians = percent * PI * 2.0

            val noiseUV = Vector2(
                (1.0 + sin(percentRadians)) * scale,
                (1.0 + cos(percentRadians)) * scale)
            
            // Insert these for interesting variations, looping with percent
            val cx = simplex(1, noiseUV)
            val cy = simplex(2, noiseUV)
            val cz = simplex(3, noiseUV)

            val circles = mutableListOf<Circle>()
            (0 until numBranches).forEach() {
                val branchRadians = it * 2 * PI / numBranches
                val radius = height * golden

                (1 until numGrains).forEach() {
                    val grainsPercent = it.toDouble() / numGrains
                    val grainSize = thickness * (1.0 - grainsPercent).pow(5) // fade out away from center

                    val outerPoint = Vector2(
                        radius * cos(percentRadians - grainsPercent  * PI - branchRadians),
                        radius * sin(percentRadians - grainsPercent * PI + branchRadians))

                    circles.add(
                        Circle(
                            mix(
                                Vector2(0.0, 0.0),
                                outerPoint,
                                grainsPercent),
                            grainSize))
                }
            }
            drawer.circles(circles)

            if (frameCount == totalFrames) {
                // Uncomment when using ScreenRecorder
                //application.exit()
            }
        }
    }
}
