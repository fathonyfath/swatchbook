package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

val ResizeHandleWidth = 24.dp

// Material's drag handle sizes. Its own VerticalDragHandle snaps between them and only notices
// a press on the pill itself, which is why the handle is drawn here.
private val RestingWidth = 4.dp
private val RestingHeight = 48.dp
private val HeldWidth = 12.dp
private val HeldHeight = 52.dp

private val GestureExclusionHeight = 120.dp

/**
 * A full-height strip between two panes, dragged sideways to resize them. The pill on it grows
 * and darkens while the strip is pressed or dragged.
 *
 * @param onDrag the distance dragged towards the end side since the last call.
 * @param onDragEnd the drag was released or interrupted.
 */
@Composable
fun ResizeHandle(
    onDragStart: () -> Unit,
    onDrag: (Dp) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val interactions = remember { MutableInteractionSource() }
    val isDragged by interactions.collectIsDraggedAsState()
    var isPressed by remember { mutableStateOf(false) }
    val isHeld = isPressed || isDragged

    val motion = MaterialTheme.motionScheme
    val pillWidth by animateDpAsState(
        targetValue = if (isHeld) HeldWidth else RestingWidth,
        animationSpec = motion.fastSpatialSpec(),
    )
    val pillHeight by animateDpAsState(
        targetValue = if (isHeld) HeldHeight else RestingHeight,
        animationSpec = motion.fastSpatialSpec(),
    )
    val pillColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.run { if (isHeld) onSurface else outline },
        animationSpec = motion.fastEffectsSpec(),
    )

    Box(
        modifier = modifier
            .width(ResizeHandleWidth)
            .fillMaxHeight()
            // With a pane collapsed the handle sits at the window's edge, where a system back
            // swipe would otherwise take the drag. The system only honours a short stretch
            // per edge, so just the pill's surroundings are claimed.
            .claimEdgeSwipes { coordinates ->
                val reach = with(density) { GestureExclusionHeight.toPx() } / 2
                val center = coordinates.size.height / 2f
                Rect(0f, center - reach, coordinates.size.width.toFloat(), center + reach)
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    // Also returns once the drag below takes the gesture over; isDragged
                    // carries the held state from there.
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
            .draggable(
                state = rememberDraggableState { delta -> onDrag(with(density) { delta.toDp() }) },
                orientation = Orientation.Horizontal,
                interactionSource = interactions,
                onDragStarted = { onDragStart() },
                onDragStopped = { onDragEnd() },
                reverseDirection = LocalLayoutDirection.current == LayoutDirection.Rtl,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Spacer(Modifier.size(pillWidth, pillHeight).background(pillColor, CircleShape))
    }
}

@ThemePreviews
@Composable
private fun ResizeHandlePreview() {
    PreviewSurface {
        Box(Modifier.size(width = ResizeHandleWidth, height = 200.dp)) {
            ResizeHandle(onDragStart = {}, onDrag = {}, onDragEnd = {})
        }
    }
}
