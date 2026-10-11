package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val DefaultListPaneWidth = 360.dp

internal val MinPaneWidth = 240.dp

/**
 * Lays a pane's content out at [width] whatever the pane's own width is at the moment, held at
 * the start and clipped where it does not fit.
 */
internal fun Modifier.contentWidth(width: Dp): Modifier =
    clipToBounds().wrapContentWidth(Alignment.Start, unbounded = true).width(width)
