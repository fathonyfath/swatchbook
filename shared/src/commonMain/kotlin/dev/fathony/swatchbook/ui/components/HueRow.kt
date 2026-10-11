package dev.fathony.swatchbook.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.data.hueLabel
import dev.fathony.swatchbook.data.shadeOf
import dev.fathony.swatchbook.data.toHex
import dev.fathony.swatchbook.ui.icons.AppIcons
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

@Composable
fun HueRow(
    hue: Int,
    isFavorite: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
) {
    val color = shadeOf(hue)
    ListItem(
        selected = isSelected,
        onClick = onClick,
        modifier = modifier,
        leadingContent = { SmallSwatch(color) },
        supportingContent = { Text(color.toHex()) },
        trailingContent = {
            if (isFavorite) {
                Icon(
                    imageVector = AppIcons.Star,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    ) {
        Text(hueLabel(hue))
    }
}

@ThemePreviews
@Composable
private fun HueRowPreview() {
    PreviewSurface { HueRow(hue = 5, isFavorite = true, onClick = {}) }
}
