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
import androidx.compose.runtime.getValue
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
 * width is shared. Neither pane can be made narrower than a usable minimum: dragging past it
 * moves only the handle a little further, with growing resistance, and it springs back on
 * release. The panes themselves stay as they are.
 *
 * Both panes always show; [CollapsibleTwoPane] is the one that lets either go.
 *
 * @param listWidth the list's preferred width; the detail takes what remains.
 */
@Composable
fun ResizableTwoPane(
    listWidth: Dp,
    onListWidthChange: (Dp) -> Unit,
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        val widest = (maxWidth - ResizeHandleWidth - MinPaneWidth).coerceAtLeast(MinPaneWidth)
        val limitedWidth = listWidth.coerceIn(MinPaneWidth, widest)
        // How far, in dp, the handle is pulled past a limit. Only ever shown, never
        // reported: the width the caller keeps stays within the limits.
        val overshoot = remember { Animatable(0f) }
        val springBack = MaterialTheme.motionScheme.fastSpatialSpec<Float>()
        val scope = rememberCoroutineScope()
        Row {
            // Each pane leaves the far side's system bar inset to the other.
            Box(
                Modifier
                    .width(limitedWidth)
                    .consumeWindowInsets(WindowInsets.systemBars.only(WindowInsetsSides.End)),
            ) {
                list()
            }
            // Where the drag would put the edge if nothing limited it. Following this rather
            // than the limited width keeps the handle under the finger: after overshooting a
            // limit, the finger has to come all the way back before the pane moves freely
            // again.
            var draggedWidth by remember { mutableStateOf(limitedWidth) }
            ResizeHandle(
                onDragStart = {
                    draggedWidth = limitedWidth
                    scope.launch { overshoot.snapTo(0f) }
                },
                onDrag = { delta ->
                    draggedWidth += delta
                    val limited = draggedWidth.coerceIn(MinPaneWidth, widest)
                    onListWidthChange(limited)
                    scope.launch { overshoot.snapTo(resisted(draggedWidth - limited).value) }
                },
                onDragEnd = { scope.launch { overshoot.animateTo(0f, springBack) } },
                // Drawn above the panes, which it moves over without pushing.
                modifier = Modifier.zIndex(1f).offset(x = overshoot.value.dp),
            )
            Box(
                Modifier
                    .weight(1f)
                    // Matches the gap the handle leaves on the detail's other side.
                    .padding(end = ResizeHandleWidth / 2)
                    .consumeWindowInsets(WindowInsets.systemBars.only(WindowInsetsSides.Start)),
            ) {
                detail()
            }
        }
    }
}

@WideThemePreviews
@Composable
private fun ResizableTwoPanePreview() {
    PreviewSurface {
        var listWidth by remember { mutableStateOf(DefaultListPaneWidth) }
        ResizableTwoPane(
            listWidth = listWidth,
            onListWidthChange = { listWidth = it },
            list = { Text("List") },
            detail = { Text("Detail") },
        )
    }
}
