package dev.fathony.swatchbook.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.window.core.layout.WindowSizeClass
import dev.fathony.swatchbook.FormFactor
import dev.fathony.swatchbook.data.AppSettings
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.navigation.TabRoute
import dev.fathony.swatchbook.navigation.TabStackSaver
import dev.fathony.swatchbook.navigation.transitions.TabTransition
import dev.fathony.swatchbook.ui.components.NavigationLayout
import dev.fathony.swatchbook.ui.components.twopane.DefaultListPaneWidth

/**
 * The three tabs and the navigation between them. Each tab keeps its own back stack, and a tab
 * that is not showing keeps its screens' state, scroll positions included.
 */
@Composable
fun TabsHost(
    favorites: SnapshotStateList<Int>,
    settings: AppSettings,
    wallpaperColorsAvailable: Boolean,
    formFactor: FormFactor,
) {
    var selected by rememberSaveable { mutableStateOf(Tab.entries.first()) }
    val backStacks = Tab.entries.map { tab ->
        key(tab) {
            rememberSaveable(saver = TabStackSaver) { mutableStateListOf(TabRoute.Home) }
        }
    }
    val selectedStack = backStacks[selected.ordinal]

    // Last in system back's order: with nothing left to pop, return to the first tab. Its
    // condition excludes the tab stack's own, so the two can never compete.
    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = selectedStack.size == 1 && selected != Tab.entries.first(),
        onBackCompleted = { selected = Tab.entries.first() },
    )

    val tabStates = rememberSaveableStateHolder()
    // One width for every tab, so the split does not jump when switching between them.
    val listPaneWidth = rememberSaveable { mutableFloatStateOf(DefaultListPaneWidth.value) }
    // Material's guidance: a navigation bar in compact-width windows, a rail in wider ones.
    // List and detail side by side only from expanded width, where both panes have room.
    // A phone in landscape can be as wide as a tablet: it gets the rail, which spares its
    // short height, but stays single pane.
    val window = currentWindowAdaptiveInfoV2().windowSizeClass
    val isLandscapePhone = formFactor == FormFactor.Phone && window.isCompactHeight
    val useRail = window.isMediumWidthOrWider
    val twoPane = window.isExpandedWidth && !isLandscapePhone
    NavigationLayout(
        useRail = useRail,
        selected = selected,
        onSelect = { tab ->
            if (tab == selected) {
                selectedStack.removeRange(1, selectedStack.size)
            } else {
                selected = tab
            }
        },
    ) {
        AnimatedContent(selected, transitionSpec = { TabTransition }) { tab ->
            tabStates.SaveableStateProvider(tab.ordinal) {
                TabStack(
                    tab = tab,
                    backStack = backStacks[tab.ordinal],
                    favorites = favorites,
                    settings = settings,
                    wallpaperColorsAvailable = wallpaperColorsAvailable,
                    // The outgoing tab is still composed while it fades.
                    backEnabled = tab == selected,
                    twoPane = twoPane,
                    listPaneWidth = listPaneWidth,
                )
            }
        }
    }
}

private val WindowSizeClass.isMediumWidthOrWider: Boolean
    get() = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

private val WindowSizeClass.isExpandedWidth: Boolean
    get() = isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

private val WindowSizeClass.isCompactHeight: Boolean
    get() = !isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)
