package dev.fathony.swatchbook.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.data.Hues
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.ui.components.AppScaffold
import dev.fathony.swatchbook.ui.components.HueList
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * @param selected the hue open beside the list, when there is one to highlight.
 */
@Composable
fun ColorsScreen(favorites: List<Int>, onOpen: (hue: Int) -> Unit, selected: Int? = null) {
    AppScaffold(title = Tab.Colors.label) { padding ->
        HueList(Hues, favorites, onOpen, Modifier.padding(padding), selected = selected)
    }
}

@ThemePreviews
@Composable
private fun ColorsScreenPreview() {
    PreviewSurface { ColorsScreen(favorites = listOf(5), onOpen = {}) }
}
