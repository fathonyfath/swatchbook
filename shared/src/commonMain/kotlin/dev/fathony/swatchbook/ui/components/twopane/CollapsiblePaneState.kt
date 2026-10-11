package dev.fathony.swatchbook.ui.components.twopane

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Which of the two panes are showing.
 */
enum class PaneSplit {
    Both,
    ListOnly,
    DetailOnly,
}

/**
 * What a [CollapsibleTwoPane] shows, and the rules that follow from a pane being collapsed:
 * the two then behave as a single pane does, one screen at a time.
 */
@Stable
class CollapsiblePaneState internal constructor(
    private val sharedListWidth: MutableFloatState,
    split: PaneSplit,
    private val hasDetail: () -> Boolean,
) {
    // What the panes were last left as. Out of date once a screen shown alone has closed,
    // until the panes have settled: see split.
    internal var chosenSplit by mutableStateOf(split)
        private set

    // Null until the layout has first placed the panes.
    private var settledSplit by mutableStateOf<PaneSplit?>(null)

    /**
     * Which panes show, or are on their way to showing. When a screen shown alone closes, its
     * list is what is left, alone.
     */
    val split: PaneSplit
        get() = when {
            chosenSplit == PaneSplit.DetailOnly && !hasDetail() -> PaneSplit.ListOnly
            else -> chosenSplit
        }

    /**
     * True when the detail has the whole width, and so needs its own way back.
     */
    val showsDetailAlone: Boolean
        get() = split == PaneSplit.DetailOnly

    /**
     * True while the panes are still moving towards [split].
     */
    val isSliding: Boolean
        get() = settledSplit.let { it != null && it != split }

    /**
     * True when the detail covers the list as a sheet, or is sliding on or off it, rather
     * than sitting beside it. The slide is then the transition between screens.
     */
    val detailIsSheet: Boolean
        get() = split != PaneSplit.Both || isSliding

    /**
     * To be called when a screen opens in the detail pane: picked from a list shown alone, it
     * opens alone.
     */
    fun detailOpened() {
        if (split != PaneSplit.Both) chosenSplit = PaneSplit.DetailOnly
    }

    internal val listWidth: Dp
        get() = sharedListWidth.floatValue.dp

    // With nothing open the detail has nothing worth the whole width.
    internal val listCollapsible: Boolean
        get() = hasDetail()

    internal fun release(split: PaneSplit, listWidth: Dp) {
        chosenSplit = split
        if (split == PaneSplit.Both) sharedListWidth.floatValue = listWidth.value
    }

    internal fun settled() {
        chosenSplit = split
        settledSplit = split
    }
}

/**
 * @param listWidth the list's width in dp while both panes show. Passed in rather than owned,
 *   so that several layouts can share one.
 * @param hasDetail whether a screen is open in the detail pane, read from snapshot state.
 */
@Composable
fun rememberCollapsiblePaneState(
    listWidth: MutableFloatState,
    hasDetail: () -> Boolean,
): CollapsiblePaneState = rememberSaveable(
    listWidth,
    saver = Saver(
        save = { it.chosenSplit },
        restore = { CollapsiblePaneState(listWidth, it, hasDetail) },
    ),
) {
    CollapsiblePaneState(listWidth, PaneSplit.Both, hasDetail)
}
