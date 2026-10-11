package dev.fathony.swatchbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fathony.swatchbook.navigation.SettingsPage
import dev.fathony.swatchbook.ui.components.AppScaffold
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

@Composable
fun AboutScreen(onBack: (() -> Unit)?) {
    AppScaffold(title = SettingsPage.About.title, onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                "Swatchbook walks around the color wheel in 72 steps of five degrees. Each " +
                    "hue comes in nine shades, from dark to light, and any hue can be kept " +
                    "as a favorite.",
            )
            Text(
                "It is built with Jetpack Compose, Material 3 Expressive and Navigation 3, " +
                    "and kept small by its build configuration. Favorites and settings are " +
                    "held in the app's saved state: they survive rotation and the system " +
                    "reclaiming the app, but not closing it for good.",
            )
        }
    }
}

@ThemePreviews
@Composable
private fun AboutScreenPreview() {
    PreviewSurface { AboutScreen(onBack = {}) }
}
