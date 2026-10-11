package dev.fathony.swatchbook.navigation.transitions

import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut

const val ScreenFadeMillis = 200

// Starts at full speed: a slow-start curve made the same fade feel sluggish.
internal val screenFade: FiniteAnimationSpec<Float> =
    tween(ScreenFadeMillis, easing = LinearOutSlowInEasing)

/**
 * An exit that changes nothing: the screen stays put, fully opaque, for [durationMillis].
 * `ExitTransition.None` would remove it at once instead.
 */
internal fun stay(durationMillis: Int): ExitTransition =
    fadeOut(tween(durationMillis), targetAlpha = 1f)
