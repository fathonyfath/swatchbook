package dev.fathony.swatchbook.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.data.AppSettings
import dev.fathony.swatchbook.data.ThemeMode
import dev.fathony.swatchbook.navigation.SettingsPage
import dev.fathony.swatchbook.ui.components.AppScaffold
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * @param wallpaperColorsAvailable false hides the switch where the platform has none to offer.
 * @param onBack null when the screen is shown beside the settings list.
 */
@Composable
fun AppearanceScreen(
    settings: AppSettings,
    wallpaperColorsAvailable: Boolean,
    onBack: (() -> Unit)?,
) {
    AppScaffold(title = SettingsPage.Appearance.title, onBack = onBack) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            ThemeMode.entries.forEach { mode ->
                val isSelected = mode == settings.themeMode
                ListItem(
                    selected = isSelected,
                    onClick = { settings.themeMode = mode },
                    // The row is the control; the button only shows the state.
                    leadingContent = { RadioButton(selected = isSelected, onClick = null) },
                ) {
                    Text(mode.label)
                }
            }
            if (wallpaperColorsAvailable) {
                ListItem(
                    onClick = { settings.wallpaperColors = !settings.wallpaperColors },
                    supportingContent = { Text("Take the app's colors from your wallpaper") },
                    trailingContent = {
                        Switch(checked = settings.wallpaperColors, onCheckedChange = null)
                    },
                ) {
                    Text("Wallpaper colors")
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun AppearanceScreenPreview() {
    PreviewSurface {
        AppearanceScreen(AppSettings(), wallpaperColorsAvailable = true, onBack = {})
    }
}
