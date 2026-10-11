package dev.fathony.swatchbook

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * The wallpaper-derived color scheme, or null before Android 12, where there is none.
 */
val WallpaperColorScheme: (@Composable (darkTheme: Boolean) -> ColorScheme)? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        { darkTheme ->
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
    } else {
        null
    }
