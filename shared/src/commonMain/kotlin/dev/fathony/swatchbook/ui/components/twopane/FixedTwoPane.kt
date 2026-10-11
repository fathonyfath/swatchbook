package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.WideThemePreviews

private val SeparatorWidth = 24.dp

/**
 * A [list] of a set width beside a [detail] that takes the rest, with a line between them.
 * Meant for lists of short labels, such as settings categories, hence the narrow default.
 */
@Composable
fun FixedTwoPane(
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    listWidth: Dp = 280.dp,
) {
    Row(modifier) {
        // Each pane leaves the far side's system bar inset to the other.
        Box(
            Modifier
                .width(listWidth)
                .consumeWindowInsets(WindowInsets.systemBars.only(WindowInsetsSides.End)),
        ) {
            list()
        }
        Box(
            // Kept clear of the status and gesture bars, which a full-height line would cut
            // through.
            modifier = Modifier
                .width(SeparatorWidth)
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Vertical))
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            VerticalDivider()
        }
        Box(
            Modifier
                .weight(1f)
                // Matches the gap the separator leaves on the detail's other side.
                .padding(end = SeparatorWidth / 2)
                .consumeWindowInsets(WindowInsets.systemBars.only(WindowInsetsSides.Start)),
        ) {
            detail()
        }
    }
}

@WideThemePreviews
@Composable
private fun FixedTwoPanePreview() {
    PreviewSurface {
        FixedTwoPane(
            list = { Text("List") },
            detail = { Text("Detail") },
        )
    }
}
