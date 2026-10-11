package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sign

// The furthest the handle goes past a limit, however far the drag does.
private val MaxOvershoot = 32.dp

/**
 * What is shown of a drag that went [overshoot] past a limit: about half of it at first, then
 * less and less, approaching [MaxOvershoot].
 */
internal fun resisted(overshoot: Dp): Dp {
    val max = MaxOvershoot.value
    val stretch = max * (1 - 1 / (abs(overshoot.value) * 0.55f / max + 1))
    return (stretch * sign(overshoot.value)).dp
}
