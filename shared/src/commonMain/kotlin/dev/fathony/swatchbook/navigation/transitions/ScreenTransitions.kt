package dev.fathony.swatchbook.navigation.transitions

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

// Between screens shown one at a time, as on a phone.

val PushTransition: ContentTransform = fadeIn(screenFade) togetherWith stay(ScreenFadeMillis)

val PopTransition: ContentTransform = EnterTransition.None togetherWith fadeOut(screenFade)

/**
 * During a back swipe NavDisplay only has to keep both screens composed: the card itself is
 * drawn by [dev.fathony.swatchbook.navigation.backSwipe].
 */
val HeldTransition: ContentTransform = EnterTransition.None togetherWith stay(durationMillis = 1)
