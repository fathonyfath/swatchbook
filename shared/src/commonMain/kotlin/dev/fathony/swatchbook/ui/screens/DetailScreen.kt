package dev.fathony.swatchbook.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fathony.swatchbook.data.DefaultShade
import dev.fathony.swatchbook.data.hueLabel
import dev.fathony.swatchbook.data.shadeOf
import dev.fathony.swatchbook.ui.components.AppScaffold
import dev.fathony.swatchbook.ui.components.LargeSwatch
import dev.fathony.swatchbook.ui.components.ShadeStrip
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * @param onBack null when the screen is shown beside its list, where there is nothing to go
 *   back to.
 */
@Composable
fun DetailScreen(
    hue: Int,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onBack: (() -> Unit)?,
) {
    // Belongs to this visit: saved with the screen's stack entry, gone once it is popped.
    var shade by rememberSaveable { mutableIntStateOf(DefaultShade) }
    AppScaffold(title = hueLabel(hue), onBack = onBack) { padding ->
        Column(
            // Scrollable so that everything stays reachable in landscape.
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LargeSwatch(shadeOf(hue, shade), Modifier.padding(horizontal = 16.dp))
            Text(
                text = "Shades",
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall,
            )
            ShadeStrip(hue = hue, selected = shade, onSelect = { shade = it })
            Button(
                onClick = onToggleFavorite,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            ) {
                Text(if (isFavorite) "Remove from favorites" else "Add to favorites")
            }
        }
    }
}

@ThemePreviews
@Composable
private fun DetailScreenPreview() {
    PreviewSurface {
        DetailScreen(hue = 210, isFavorite = false, onToggleFavorite = {}, onBack = {})
    }
}
