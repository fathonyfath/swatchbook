package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates

/**
 * Keeps drags that start in [rect] from being taken as a system edge swipe, on platforms that
 * have one.
 */
internal expect fun Modifier.claimEdgeSwipes(rect: (LayoutCoordinates) -> Rect): Modifier
