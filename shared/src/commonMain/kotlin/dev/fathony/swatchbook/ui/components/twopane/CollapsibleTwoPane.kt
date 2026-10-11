package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.WideThemePreviews
import kotlinx.coroutines.launch

/**
 * A [list] beside a [detail], with a handle between them that is dragged to change how the
 * width is shared, or all the way to an edge to collapse a pane and leave the other alone.
 * The handle stays at that edge, from where the pane is dragged back out.
 *
 * The handle follows the drag freely. A pane cannot rest narrower than a usable minimum, so
 * on release one left in between settles at that minimum or collapses, whichever is nearer.
 *
 * Content changes width gradually only for a drag: under the finger, and while the panes
 * settle after it is let go. When they change places on their own, each is already laid out
 * at the width it will end up with, and the detail slides over the list like a sheet: nothing
 * reflows on the way.
 *
 * While nothing is open in the detail pane the list cannot collapse: dragging it below its
 * minimum then moves only the handle, with the resistance and spring back of
 * [ResizableTwoPane].
 *
 * Unlike there, the new width and split reach [state] on release, not during the drag.
 */
@Composable
fun CollapsibleTwoPane(
    state: CollapsiblePaneState,
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val total = (maxWidth - ResizeHandleWidth).coerceAtLeast(0.dp)
        val widest = (total - MinPaneWidth).coerceAtLeast(MinPaneWidth)
        val split = state.split
        val listCollapsible = state.listCollapsible
        val splitWidth = state.listWidth.coerceIn(MinPaneWidth, widest)
        val restingWidth = when (split) {
            PaneSplit.DetailOnly -> 0.dp
            PaneSplit.Both -> splitWidth
            PaneSplit.ListOnly -> total
        }
        // The list's width on screen, in dp: under the finger during a drag, otherwise at or
        // on its way to the resting width.
        val shown = remember { Animatable(restingWidth.value) }
        val settle = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
        // How far, in dp, the handle is pulled past the minimum of a list that cannot collapse.
        val overshoot = remember { Animatable(0f) }
        val springBack = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
        val scope = rememberCoroutineScope()
        // True from the start of a drag until the panes have settled after it: while it is,
        // their content follows the width on screen.
        var resizing by remember { mutableStateOf(false) }
        // Follows changes from outside a drag: a screen opened or closed with a pane
        // collapsed, or a resized window. Also catches up with any that arrived during one.
        LaunchedEffect(split, restingWidth, resizing) {
            if (!resizing) {
                shown.animateTo(restingWidth.value, settle)
                state.settled()
            }
        }

        val listShownWidth = shown.value.dp.coerceIn(0.dp, total)
        // A collapsed pane has no width of its own to end up with, so it keeps the one it had.
        val listContentWidth = lastKnown(
            width = when {
                resizing -> listShownWidth.coerceAtLeast(MinPaneWidth)
                split == PaneSplit.ListOnly -> total
                split == PaneSplit.Both -> splitWidth
                else -> null
            },
            initial = splitWidth,
        )
        val detailContentWidth = lastKnown(
            width = when {
                resizing -> (total - listShownWidth).coerceAtLeast(MinPaneWidth)
                split == PaneSplit.DetailOnly -> total
                split == PaneSplit.Both -> total - splitWidth
                else -> null
            },
            initial = total - splitWidth,
        )
        Row {
            // Each pane leaves the far side's system bar inset to the other, while there is
            // another.
            Box(
                Modifier
                    .width(listShownWidth)
                    .contentWidth(listContentWidth)
                    .then(
                        if (split == PaneSplit.ListOnly) Modifier
                        else Modifier.consumeWindowInsets(
                            WindowInsets.systemBars.only(WindowInsetsSides.End),
                        ),
                    ),
            ) {
                list()
            }
            // Where the drag would put the edge if nothing limited it, which keeps the handle
            // under the finger after it has been dragged past a limit.
            var draggedWidth by remember { mutableStateOf(restingWidth) }
            val narrowest = if (listCollapsible) 0.dp else MinPaneWidth
            val reachable = narrowest..total.coerceAtLeast(narrowest)
            ResizeHandle(
                onDragStart = {
                    draggedWidth = shown.value.dp
                    resizing = true
                    scope.launch { overshoot.snapTo(0f) }
                },
                onDrag = { delta ->
                    draggedWidth += delta
                    val width = draggedWidth.coerceIn(reachable)
                    // A list that cannot collapse resists instead: the handle alone goes a
                    // little further, and springs back when the drag is let go.
                    val pastMinimum =
                        if (listCollapsible) 0.dp
                        else (draggedWidth - narrowest).coerceAtMost(0.dp)
                    scope.launch {
                        shown.snapTo(width.value)
                        overshoot.snapTo(resisted(pastMinimum).value)
                    }
                },
                onDragEnd = {
                    val released = draggedWidth.coerceIn(reachable)
                    val settled = when {
                        released < MinPaneWidth / 2 -> PaneSplit.DetailOnly
                        released > (widest + total) / 2 -> PaneSplit.ListOnly
                        else -> PaneSplit.Both
                    }
                    val settledWidth = when (settled) {
                        PaneSplit.DetailOnly -> 0.dp
                        PaneSplit.Both -> released.coerceIn(MinPaneWidth, widest)
                        PaneSplit.ListOnly -> total
                    }
                    state.release(settled, settledWidth)
                    scope.launch { overshoot.animateTo(0f, springBack) }
                    // A new drag interrupts this, and then resizing rightly stays set.
                    scope.launch {
                        shown.animateTo(settledWidth.value, settle)
                        resizing = false
                    }
                },
                // Drawn above the panes, which it moves over without pushing.
                modifier = Modifier.zIndex(1f).offset(x = overshoot.value.dp),
            )
            Box(
                Modifier
                    .weight(1f)
                    .contentWidth(detailContentWidth)
                    // Matches the gap the handle leaves on the detail's other side.
                    .padding(end = ResizeHandleWidth / 2)
                    .then(
                        if (split == PaneSplit.DetailOnly) Modifier
                        else Modifier.consumeWindowInsets(
                            WindowInsets.systemBars.only(WindowInsetsSides.Start),
                        ),
                    ),
            ) {
                detail()
            }
        }
    }
}

/**
 * [width], or while it is null the last one that was not.
 */
@Composable
private fun lastKnown(width: Dp?, initial: Dp): Dp {
    val last = remember { LastWidth(initial) }
    if (width != null) last.value = width
    return last.value
}

private class LastWidth(var value: Dp)

@WideThemePreviews
@Composable
private fun CollapsibleTwoPanePreview() {
    PreviewSurface {
        val listWidth = remember { mutableFloatStateOf(DefaultListPaneWidth.value) }
        CollapsibleTwoPane(
            state = rememberCollapsiblePaneState(listWidth, hasDetail = { true }),
            list = { Text("List") },
            detail = { Text("Detail") },
        )
    }
}
