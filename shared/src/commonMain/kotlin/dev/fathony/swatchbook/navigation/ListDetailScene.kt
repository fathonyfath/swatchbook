package dev.fathony.swatchbook.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import dev.fathony.swatchbook.navigation.transitions.PaneTransition

private const val ListPaneKey = "listPane"

/**
 * Entry metadata marking a screen as the list of a list-detail pair.
 */
val ListPaneMetadata: Map<String, Any> = mapOf(ListPaneKey to true)

private val NavEntry<*>.isListPane: Boolean
    get() = ListPaneKey in metadata

/**
 * Arranges a list pane and a detail pane on screen.
 */
typealias PaneLayout =
    @Composable (list: @Composable () -> Unit, detail: @Composable () -> Unit) -> Unit

/**
 * Shows a list screen and the screen opened from it side by side, with [placeholder] in the
 * detail pane while nothing is open. The back stack is the same as in a single pane; only what
 * is on screen differs.
 *
 * How the two panes are arranged is left to [layout], so each use can have its own.
 *
 * @param detailTransition how the detail pane changes what it shows.
 * @param holdsClosedDetail true while a screen that has just closed should stay in the detail
 *   pane instead of giving way to the [placeholder], such as on a pane still sliding away.
 */
class ListDetailSceneStrategy<T : Any>(
    private val layout: PaneLayout,
    private val placeholder: @Composable () -> Unit,
    private val detailTransition: () -> ContentTransform = { PaneTransition },
    private val holdsClosedDetail: () -> Boolean = { false },
) : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val top = entries.last()
        val beneath = entries.getOrNull(entries.lastIndex - 1)
        return when {
            top.isListPane -> scene(list = top, detail = null, entries)
            beneath?.isListPane == true -> scene(list = beneath, detail = top, entries)
            else -> null
        }
    }

    private fun scene(list: NavEntry<T>, detail: NavEntry<T>?, entries: List<NavEntry<T>>) =
        ListDetailScene(
            list = list,
            detail = detail,
            previousEntries = entries.dropLast(1),
            layout = layout,
            placeholder = placeholder,
            detailTransition = detailTransition,
            holdsClosedDetail = holdsClosedDetail,
        )
}

private data class ListDetailScene<T : Any>(
    val list: NavEntry<T>,
    val detail: NavEntry<T>?,
    override val previousEntries: List<NavEntry<T>>,
    val layout: PaneLayout,
    val placeholder: @Composable () -> Unit,
    val detailTransition: () -> ContentTransform,
    val holdsClosedDetail: () -> Boolean,
) : Scene<T> {
    // The list's key alone, so that opening, changing or closing the detail updates this scene
    // in place. A transition between two scenes would hand the list from one to the other and
    // redraw it.
    override val key: Any = list.contentKey

    override val entries: List<NavEntry<T>> = listOfNotNull(list, detail)

    override val content: @Composable () -> Unit = {
        layout(
            { list.Content() },
            {
                val lastShown = remember { LastShown<T>() }
                val shown = if (detail == null && holdsClosedDetail()) lastShown.entry else detail
                lastShown.entry = shown
                AnimatedContent(
                    targetState = shown,
                    transitionSpec = { detailTransition() },
                    contentKey = { it?.contentKey },
                ) { entry ->
                    if (entry == null) placeholder() else entry.Content()
                }
            },
        )
    }
}

private class LastShown<T : Any> {
    var entry: NavEntry<T>? = null
}
