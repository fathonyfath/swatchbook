package dev.fathony.swatchbook.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import dev.fathony.swatchbook.ui.icons.AppIcons

/**
 * The bottom-bar tabs, in display order. The first is where system back ends up.
 */
enum class Tab(val label: String, val icon: ImageVector) {
    Colors("Colors", AppIcons.Grid),
    Favorites("Favorites", AppIcons.Star),
    Settings("Settings", AppIcons.Settings),
}
