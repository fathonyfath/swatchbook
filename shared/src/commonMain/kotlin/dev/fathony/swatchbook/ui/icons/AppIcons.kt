package dev.fathony.swatchbook.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * The app's icons, drawn here rather than taken from an icon library: the grid is not in
 * material-icons-core, and vector drawables would tie shared code to Android resources.
 */
object AppIcons {
    val ArrowBack: ImageVector by lazy {
        icon("ArrowBack", autoMirror = true) {
            moveTo(20f, 11f)
            horizontalLineTo(7.83f)
            lineToRelative(5.59f, -5.59f)
            lineTo(12f, 4f)
            lineToRelative(-8f, 8f)
            lineToRelative(8f, 8f)
            lineToRelative(1.41f, -1.41f)
            lineTo(7.83f, 13f)
            horizontalLineTo(20f)
            close()
        }
    }

    val Grid: ImageVector by lazy {
        icon("Grid") {
            for (x in listOf(3f, 13f)) {
                for (y in listOf(3f, 13f)) {
                    moveTo(x, y)
                    horizontalLineToRelative(8f)
                    verticalLineToRelative(8f)
                    horizontalLineToRelative(-8f)
                    close()
                }
            }
        }
    }

    val Star: ImageVector by lazy {
        icon("Star") {
            moveTo(12f, 17.27f)
            lineTo(18.18f, 21f)
            lineToRelative(-1.64f, -7.03f)
            lineTo(22f, 9.24f)
            lineToRelative(-7.19f, -0.61f)
            lineTo(12f, 2f)
            lineTo(9.19f, 8.63f)
            lineTo(2f, 9.24f)
            lineToRelative(5.46f, 4.73f)
            lineTo(5.82f, 21f)
            close()
        }
    }

    val Settings: ImageVector by lazy {
        icon("Settings") {
            // A ring with eight teeth around a round hole.
            val teeth = 8
            val outer = 9.5f
            val inner = 7f
            for (i in 0 until teeth * 4) {
                // Each tooth is two points out and two points in, a quarter step apart.
                val radius = if (i % 4 < 2) outer else inner
                val angle = (i - 0.5f) * (2f * PI.toFloat() / (teeth * 4))
                val x = 12f + radius * cos(angle)
                val y = 12f + radius * sin(angle)
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
            // Drawn against the outline's direction, which makes it a hole.
            moveTo(15.5f, 12f)
            arcToRelative(3.5f, 3.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, -7f, 0f)
            arcToRelative(3.5f, 3.5f, 0f, isMoreThanHalf = true, isPositiveArc = false, 7f, 0f)
            close()
        }
    }
}

/**
 * A single-path 24dp icon; the fill is replaced by the tint wherever it is drawn.
 */
private fun icon(
    name: String,
    autoMirror: Boolean = false,
    path: PathBuilder.() -> Unit,
): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
        autoMirror = autoMirror,
    ).path(fill = SolidColor(Color.Black), pathBuilder = path).build()
