package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates

internal actual fun Modifier.claimEdgeSwipes(rect: (LayoutCoordinates) -> Rect): Modifier =
    systemGestureExclusion(rect)
