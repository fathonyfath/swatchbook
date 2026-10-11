package dev.fathony.swatchbook.navigation.transitions

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

/**
 * In the detail pane of a list-detail pair, one screen fades into the next.
 */
val PaneTransition: ContentTransform = fadeIn(screenFade) togetherWith fadeOut(screenFade)

/**
 * In a detail pane that slides over its list as a sheet. The slide is the transition, so the
 * screen on the sheet changes at once, with no fade of its own.
 */
val SheetTransition: ContentTransform = EnterTransition.None togetherWith ExitTransition.None
