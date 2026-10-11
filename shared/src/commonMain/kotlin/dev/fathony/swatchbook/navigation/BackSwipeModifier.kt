package dev.fathony.swatchbook.navigation

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

private const val MinScale = 0.9f
private val CardCorner = 28.dp
private val EdgeGap = 8.dp

// The corners and the scrim finish within the first eighth of the swipe, so the card reads as
// a card almost immediately instead of rounding off gradually.
private const val QuickPhase = 8f

/**
 * Draws a screen's part in a back swipe, modelled on Android's own animation between
 * activities. The screen with [contentKey] equal to the swiped key becomes a card that shrinks,
 * rounds its corners and follows the finger; any other screen is the one being returned to,
 * which stays put under a scrim.
 *
 * State is read only while drawing, so a swipe never recomposes the screens.
 */
fun Modifier.backSwipe(state: BackSwipeState, contentKey: Any, scrimAlpha: Float): Modifier =
    graphicsLayer {
        if (state.swipedKey != contentKey) return@graphicsLayer
        val progress = state.progress
        val scale = 1f - (1f - MinScale) * progress
        scaleX = scale
        scaleY = scale
        // Shrinking leaves half of the lost size as a margin on each side. Shift by that
        // margin less the gap, so the card ends up exactly EdgeGap from the edge.
        val margin = (1f - MinScale) / 2f
        val gap = EdgeGap.toPx()
        translationX =
            state.direction * progress * (size.width * margin - gap).coerceAtLeast(0f)
        // Full vertical shift once the finger has travelled half the screen's height.
        val vertical = (state.dragY / (size.height / 2f)).coerceIn(-1f, 1f)
        translationY = vertical * progress * (size.height * margin - gap).coerceAtLeast(0f)
        alpha = state.alpha
        shape = RoundedCornerShape(CardCorner * (progress * QuickPhase).coerceAtMost(1f))
        clip = true
    }.drawWithContent {
        drawContent()
        val swiped = state.swipedKey
        if (swiped != null && swiped != contentKey) {
            val scrim = (state.progress * QuickPhase).coerceAtMost(1f) * state.alpha * scrimAlpha
            drawRect(Color.Black, alpha = scrim)
        }
    }
