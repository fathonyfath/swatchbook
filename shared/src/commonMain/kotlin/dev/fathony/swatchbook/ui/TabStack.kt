package dev.fathony.swatchbook.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import dev.fathony.swatchbook.data.AppSettings
import dev.fathony.swatchbook.navigation.ListDetailSceneStrategy
import dev.fathony.swatchbook.navigation.ListPaneMetadata
import dev.fathony.swatchbook.navigation.PaneLayout
import dev.fathony.swatchbook.navigation.SettingsPage
import dev.fathony.swatchbook.navigation.SwipeNavDisplay
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.navigation.TabRoute
import dev.fathony.swatchbook.navigation.transitions.PaneTransition
import dev.fathony.swatchbook.navigation.transitions.SheetTransition
import dev.fathony.swatchbook.ui.components.EmptyState
import dev.fathony.swatchbook.ui.components.twopane.CollapsibleTwoPane
import dev.fathony.swatchbook.ui.components.twopane.FixedTwoPane
import dev.fathony.swatchbook.ui.components.twopane.ResizableTwoPane
import dev.fathony.swatchbook.ui.components.twopane.rememberCollapsiblePaneState
import dev.fathony.swatchbook.ui.screens.AboutScreen
import dev.fathony.swatchbook.ui.screens.AppearanceScreen
import dev.fathony.swatchbook.ui.screens.ColorsScreen
import dev.fathony.swatchbook.ui.screens.DetailScreen
import dev.fathony.swatchbook.ui.screens.FavoritesScreen
import dev.fathony.swatchbook.ui.screens.FavoritesSettingsScreen
import dev.fathony.swatchbook.ui.screens.SettingsScreen
import dev.fathony.swatchbook.ui.screens.favoritesSummary

/**
 * One tab's screens on that tab's own back stack: a list, and whatever is opened from it.
 *
 * @param twoPane shows the list and the opened screen side by side.
 * @param listPaneWidth the list's width in dp when side by side, where the user can change it.
 */
@Composable
fun TabStack(
    tab: Tab,
    backStack: SnapshotStateList<TabRoute>,
    favorites: SnapshotStateList<Int>,
    settings: AppSettings,
    wallpaperColorsAvailable: Boolean,
    backEnabled: Boolean,
    twoPane: Boolean,
    listPaneWidth: MutableFloatState,
) {
    val opened = backStack.last().takeIf { twoPane }
    // Only the Colors tab lets a pane collapse; for the others this stays at both panes.
    val panes = rememberCollapsiblePaneState(
        listWidth = listPaneWidth,
        hasDetail = { backStack.last() != TabRoute.Home },
    )
    // Replaces an open screen rather than stacking a second one: beside the list a tap picks
    // another row, and in a single pane it absorbs a double tap.
    val open: (TabRoute) -> Unit = { route ->
        if (backStack.last() == TabRoute.Home) {
            backStack.add(route)
        } else {
            backStack[backStack.lastIndex] = route
        }
        panes.detailOpened()
    }
    // Beside its list a screen has nothing to go back to, so it gets no back arrow.
    val onBack: (() -> Unit)? =
        if (twoPane && !panes.showsDetailAlone) null
        else ({ backStack.removeRange(1, backStack.size) })
    val sceneStrategies = remember(tab, listPaneWidth, panes) {
        val layout: PaneLayout = when (tab) {
            Tab.Colors -> { list, detail ->
                CollapsibleTwoPane(state = panes, list = list, detail = detail)
            }
            Tab.Favorites -> { list, detail ->
                ResizableTwoPane(
                    listWidth = listPaneWidth.floatValue.dp,
                    onListWidthChange = { listPaneWidth.floatValue = it.value },
                    list = list,
                    detail = detail,
                )
            }
            Tab.Settings -> { list, detail -> FixedTwoPane(list, detail) }
        }
        val strategy = ListDetailSceneStrategy<TabRoute>(
            layout = layout,
            // Side by side, one screen fades into the next. With a pane collapsed the detail
            // slides over the list instead, and a fade on top of that would show through.
            detailTransition = { if (panes.detailIsSheet) SheetTransition else PaneTransition },
            // A screen closed while shown alone stays on the sheet until that is out of view.
            holdsClosedDetail = { panes.isSliding },
            placeholder = {
                if (tab == Tab.Settings) {
                    EmptyState("No setting selected", "Pick a category from the list.")
                } else {
                    EmptyState("No color selected", "Pick a color from the list.")
                }
            },
        )
        listOf(strategy)
    }
    SwipeNavDisplay(
        backStack = backStack,
        backEnabled = backEnabled,
        showsSwipeCard = !twoPane,
        sceneStrategies = if (twoPane) sceneStrategies else emptyList(),
        entryProvider = entryProvider {
            entry<TabRoute.Home>(metadata = ListPaneMetadata) {
                val openHue = (opened as? TabRoute.Detail)?.hue
                val openDetail = { hue: Int -> open(TabRoute.Detail(hue)) }
                when (tab) {
                    Tab.Colors -> ColorsScreen(favorites, openDetail, openHue)
                    Tab.Favorites -> FavoritesScreen(favorites, openDetail, openHue)
                    Tab.Settings -> SettingsScreen(
                        summary = { page ->
                            when (page) {
                                SettingsPage.Appearance -> settings.themeMode.label
                                SettingsPage.Favorites -> favoritesSummary(favorites.size)
                                SettingsPage.About -> "What this app is"
                            }
                        },
                        onOpen = { page -> open(TabRoute.Settings(page)) },
                        selected = (opened as? TabRoute.Settings)?.page,
                    )
                }
            }
            entry<TabRoute.Detail> { (hue) ->
                DetailScreen(
                    hue = hue,
                    isFavorite = hue in favorites,
                    onToggleFavorite = { if (!favorites.remove(hue)) favorites.add(hue) },
                    onBack = onBack,
                )
            }
            entry<TabRoute.Settings> { (page) ->
                when (page) {
                    SettingsPage.Appearance ->
                        AppearanceScreen(settings, wallpaperColorsAvailable, onBack)
                    SettingsPage.Favorites ->
                        FavoritesSettingsScreen(favorites.size, { favorites.clear() }, onBack)
                    SettingsPage.About -> AboutScreen(onBack)
                }
            }
        },
    )
}
