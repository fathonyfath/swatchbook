package dev.fathony.swatchbook.navigation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigationevent.NavigationEvent
import dev.fathony.swatchbook.navigation.transitions.ScreenFadeMillis

private const val CancelMillis = 150

/**
 * Where a predictive back swipe currently is, for [backSwipe] to draw.
 */
class BackSwipeState {
    /**
     * Content key of the screen being swiped away, or null when no swipe is in flight.
     */
    var swipedKey: Any? by mutableStateOf(null)
        private set

    /**
     * Eased swipe progress, from 0 (untouched) to 1 (fully shrunk).
     */
    var progress by mutableFloatStateOf(0f)
        private set

    /**
     * Opacity of the card; it only drops once the swipe is released.
     */
    var alpha by mutableFloatStateOf(1f)
        private set

    /**
     * +1 when the card is pulled towards the right edge, -1 towards the left.
     */
    var direction by mutableFloatStateOf(0f)
        private set

    /**
     * Vertical finger travel since the swipe began, in pixels.
     */
    var dragY by mutableFloatStateOf(0f)
        private set

    private var startY = 0f

    fun start(key: Any, event: NavigationEvent) {
        swipedKey = key
        alpha = 1f
        startY = event.touchY
        update(event)
    }

    fun update(event: NavigationEvent) {
        progress = LinearOutSlowInEasing.transform(event.progress)
        dragY = event.touchY - startY
        direction = when (event.swipeEdge) {
            NavigationEvent.EDGE_LEFT -> 1f
            NavigationEvent.EDGE_RIGHT -> -1f
            else -> 0f
        }
    }

    suspend fun fadeOut() {
        animate(alpha, 0f, animationSpec = tween(ScreenFadeMillis)) { value, _ -> alpha = value }
    }

    suspend fun restore() {
        animate(progress, 0f, animationSpec = tween(CancelMillis)) { value, _ -> progress = value }
    }

    fun reset() {
        swipedKey = null
        progress = 0f
        alpha = 1f
    }
}
