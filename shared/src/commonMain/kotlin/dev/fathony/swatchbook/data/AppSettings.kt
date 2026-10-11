package dev.fathony.swatchbook.data

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

enum class ThemeMode(val label: String) {
    System("Follow system"),
    Light("Light"),
    Dark("Dark"),
}

/**
 * What the Settings tab changes. Held in saved state only, like the favorites.
 */
@Stable
class AppSettings(themeMode: ThemeMode = ThemeMode.System, wallpaperColors: Boolean = true) {
    var themeMode by mutableStateOf(themeMode)

    /**
     * Only has an effect where the platform can supply wallpaper colors.
     */
    var wallpaperColors by mutableStateOf(wallpaperColors)

    companion object {
        val Saver: Saver<AppSettings, Any> = listSaver(
            save = { listOf(it.themeMode.ordinal, if (it.wallpaperColors) 1 else 0) },
            restore = { AppSettings(ThemeMode.entries[it[0]], it[1] == 1) },
        )
    }
}
