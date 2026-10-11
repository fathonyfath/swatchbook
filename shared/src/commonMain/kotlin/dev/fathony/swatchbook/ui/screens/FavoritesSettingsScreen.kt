package dev.fathony.swatchbook.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.navigation.SettingsPage
import dev.fathony.swatchbook.ui.components.AppScaffold
import dev.fathony.swatchbook.ui.components.ClearFavoritesDialog
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

fun favoritesSummary(count: Int): String = if (count > 0) "$count saved" else "Nothing saved"

/**
 * @param onBack null when the screen is shown beside the settings list.
 */
@Composable
fun FavoritesSettingsScreen(
    favoriteCount: Int,
    onClearFavorites: () -> Unit,
    onBack: (() -> Unit)?,
) {
    var confirmingClear by rememberSaveable { mutableStateOf(false) }
    AppScaffold(title = SettingsPage.Favorites.title, onBack = onBack) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            ListItem(
                onClick = { confirmingClear = true },
                enabled = favoriteCount > 0,
                supportingContent = { Text(favoritesSummary(favoriteCount)) },
            ) {
                Text("Clear favorites")
            }
        }
    }
    if (confirmingClear) {
        ClearFavoritesDialog(
            count = favoriteCount,
            onDismiss = { confirmingClear = false },
            onClear = {
                confirmingClear = false
                onClearFavorites()
            },
        )
    }
}

@ThemePreviews
@Composable
private fun FavoritesSettingsScreenPreview() {
    PreviewSurface {
        FavoritesSettingsScreen(favoriteCount = 3, onClearFavorites = {}, onBack = {})
    }
}

@ThemePreviews
@Composable
private fun FavoritesSettingsScreenEmptyPreview() {
    PreviewSurface {
        FavoritesSettingsScreen(favoriteCount = 0, onClearFavorites = {}, onBack = {})
    }
}
