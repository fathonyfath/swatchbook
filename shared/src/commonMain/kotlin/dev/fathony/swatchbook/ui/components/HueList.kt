package dev.fathony.swatchbook.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

@Composable
fun HueList(
    hues: List<Int>,
    favorites: List<Int>,
    onOpen: (hue: Int) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    selected: Int? = null,
) {
    LazyColumn(modifier = modifier, contentPadding = contentPadding) {
        items(hues, key = { it }) { hue ->
            HueRow(
                hue = hue,
                isFavorite = hue in favorites,
                onClick = { onOpen(hue) },
                isSelected = hue == selected,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun HueListPreview() {
    PreviewSurface { HueList(hues = listOf(0, 30, 210), favorites = listOf(30), onOpen = {}) }
}
