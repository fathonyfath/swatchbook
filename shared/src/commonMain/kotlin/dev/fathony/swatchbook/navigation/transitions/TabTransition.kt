package dev.fathony.swatchbook.navigation.transitions

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith

val TabTransition: ContentTransform =
    fadeIn(tween(durationMillis = 120, delayMillis = 50)) togetherWith fadeOut(tween(50))
