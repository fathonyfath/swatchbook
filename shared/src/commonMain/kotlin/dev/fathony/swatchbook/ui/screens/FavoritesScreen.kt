package dev.fathony.swatchbook.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.ui.components.AppScaffold
import dev.fathony.swatchbook.ui.components.EmptyState
import dev.fathony.swatchbook.ui.components.HueList
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * @param selected the hue open beside the list, when there is one to highlight.
 */
@Composable
fun FavoritesScreen(favorites: List<Int>, onOpen: (hue: Int) -> Unit, selected: Int? = null) {
    AppScaffold(title = Tab.Favorites.label) { padding ->
        if (favorites.isEmpty()) {
            EmptyState(
                title = "No favorites yet",
                message = "Open a color and add it to your favorites.",
                modifier = Modifier.padding(padding),
            )
        } else {
            HueList(favorites, favorites, onOpen, Modifier.padding(padding), selected = selected)
        }
    }
}

@ThemePreviews
@Composable
private fun FavoritesScreenPreview() {
    PreviewSurface { FavoritesScreen(favorites = listOf(210, 5), onOpen = {}) }
}

@ThemePreviews
@Composable
private fun FavoritesScreenEmptyPreview() {
    PreviewSurface { FavoritesScreen(favorites = emptyList(), onOpen = {}) }
}
