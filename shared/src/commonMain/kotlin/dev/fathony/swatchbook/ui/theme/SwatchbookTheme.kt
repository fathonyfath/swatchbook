package dev.fathony.swatchbook.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable

/**
 * @param colorScheme a platform-provided scheme, such as Android's wallpaper colors; null uses
 *   the Material baseline.
 */
@Composable
fun SwatchbookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme? = null,
    content: @Composable () -> Unit,
) {
    MaterialExpressiveTheme(
        colorScheme = colorScheme
            ?: if (darkTheme) darkColorScheme() else expressiveLightColorScheme(),
        content = content,
    )
}
