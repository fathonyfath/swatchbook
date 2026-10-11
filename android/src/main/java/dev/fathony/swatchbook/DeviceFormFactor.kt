package dev.fathony.swatchbook

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

// The width Android itself draws the line at, as in the sw600dp resource qualifier.
private const val TabletMinWidthDp = 600

/**
 * What the device is, judged by its display rather than the app's window. An unfolded foldable
 * counts as a tablet, and so does a phone showing the app on an external display.
 */
@Composable
fun deviceFormFactor(): FormFactor {
    val context = LocalContext.current
    // Folding and unfolding change the display, and with it the configuration.
    val configuration = LocalConfiguration.current
    return remember(context, configuration) {
        when {
            context.packageManager.hasSystemFeature(PackageManager.FEATURE_PC) ->
                FormFactor.Desktop
            context.smallestDisplayWidthDp(configuration) < TabletMinWidthDp -> FormFactor.Phone
            else -> FormFactor.Tablet
        }
    }
}

private fun Context.smallestDisplayWidthDp(configuration: Configuration): Float =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val bounds = getSystemService(WindowManager::class.java).maximumWindowMetrics.bounds
        minOf(bounds.width(), bounds.height()) * 160f / configuration.densityDpi
    } else {
        // Follows the window in split screen, the closest there is before Android 11.
        configuration.smallestScreenWidthDp.toFloat()
    }
