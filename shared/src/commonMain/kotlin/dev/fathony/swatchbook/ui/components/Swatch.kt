package dev.fathony.swatchbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import dev.fathony.swatchbook.data.shadeOf
import dev.fathony.swatchbook.data.toHex
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

@Composable
fun SmallSwatch(color: Color, modifier: Modifier = Modifier) {
    Spacer(modifier.size(44.dp).background(color, MaterialTheme.shapes.small))
}

@Composable
fun LargeSwatch(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = color.toHex(),
            color = if (color.luminance() <= 0.4f) Color.White else Color.Black,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@ThemePreviews
@Composable
private fun SmallSwatchPreview() {
    PreviewSurface { SmallSwatch(shadeOf(210)) }
}

@ThemePreviews
@Composable
private fun LargeSwatchPreview() {
    PreviewSurface { LargeSwatch(shadeOf(210)) }
}
