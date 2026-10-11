package dev.fathony.swatchbook.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val Hues: List<Int> = (0 until 360 step 5).toList()

const val ShadeCount = 9

const val DefaultShade = 4

/**
 * Shade [index] of [hue], from 0 (darkest) to 8 (lightest).
 */
fun shadeOf(hue: Int, index: Int = DefaultShade): Color =
    Color.hsl(hue = hue.toFloat(), saturation = 0.7f, lightness = (index + 1) / 10f)

fun hueLabel(hue: Int): String = "${hueName(hue)} $hue°"

private fun hueName(hue: Int): String = when (hue) {
    in 15..44 -> "Orange"
    in 45..69 -> "Yellow"
    in 70..164 -> "Green"
    in 165..199 -> "Cyan"
    in 200..259 -> "Blue"
    in 260..289 -> "Purple"
    in 290..344 -> "Pink"
    else -> "Red"
}

/**
 * `#RRGGBB`. Written by hand because `String.format` is JVM-only.
 */
fun Color.toHex(): String =
    "#" + (toArgb() and 0xFFFFFF).toString(16).uppercase().padStart(6, '0')
