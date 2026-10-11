package dev.fathony.swatchbook

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.rememberSaveable
import dev.fathony.swatchbook.data.AppSettings
import dev.fathony.swatchbook.data.ThemeMode
import dev.fathony.swatchbook.navigation.IntListSaver
import dev.fathony.swatchbook.ui.TabsHost
import dev.fathony.swatchbook.ui.preview.ThemePreviews
import dev.fathony.swatchbook.ui.preview.WideThemePreviews
import dev.fathony.swatchbook.ui.theme.SwatchbookTheme

/**
 * @param wallpaperColorScheme the platform's wallpaper-derived scheme for a light or dark
 *   theme, or null where the platform has none.
 * @param onDarkThemeChange reports the theme in effect, so the platform can match what it
 *   draws itself, such as system bar icons.
 */
@Composable
fun App(
    formFactor: FormFactor,
    wallpaperColorScheme: (@Composable (darkTheme: Boolean) -> ColorScheme)? = null,
    onDarkThemeChange: (darkTheme: Boolean) -> Unit = {},
) {
    val settings = rememberSaveable(saver = AppSettings.Saver) { AppSettings() }
    val darkTheme = when (settings.themeMode) {
        ThemeMode.System -> isSystemInDarkTheme()
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
    }
    LaunchedEffect(darkTheme) { onDarkThemeChange(darkTheme) }
    val colorScheme =
        if (settings.wallpaperColors) wallpaperColorScheme?.invoke(darkTheme) else null
    SwatchbookTheme(darkTheme, colorScheme) {
        val favorites = rememberSaveable(saver = IntListSaver) { mutableStateListOf() }
        TabsHost(
            favorites = favorites,
            settings = settings,
            wallpaperColorsAvailable = wallpaperColorScheme != null,
            formFactor = formFactor,
        )
    }
}

@ThemePreviews
@WideThemePreviews
@Composable
private fun AppPreview() {
    App(FormFactor.Phone)
}
