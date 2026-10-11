package dev.fathony.swatchbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.fathony.swatchbook.data.ShadeCount
import dev.fathony.swatchbook.data.shadeOf
import dev.fathony.swatchbook.data.toHex
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

private val ShadeShape = RoundedCornerShape(8.dp)

@Composable
fun ShadeStrip(
    hue: Int,
    selected: Int,
    onSelect: (shade: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(ShadeCount) { shade ->
            val color = shadeOf(hue, shade)
            val ring =
                if (shade == selected) {
                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, ShadeShape)
                } else {
                    Modifier
                }
            Spacer(
                Modifier
                    .size(56.dp)
                    .clip(ShadeShape)
                    .background(color)
                    .then(ring)
                    .clickable(role = Role.RadioButton) { onSelect(shade) }
                    .semantics { contentDescription = color.toHex() },
            )
        }
    }
}

@ThemePreviews
@Composable
private fun ShadeStripPreview() {
    PreviewSurface { ShadeStrip(hue = 210, selected = 4, onSelect = {}) }
}
