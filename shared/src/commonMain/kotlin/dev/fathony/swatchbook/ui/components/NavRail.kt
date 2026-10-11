package dev.fathony.swatchbook.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * The side rail that replaces [BottomBar] in wide windows. [onSelect] also fires for the tab
 * that is already selected.
 */
@Composable
fun NavRail(selected: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    WideNavigationRail(modifier = modifier) {
        Tab.entries.forEach { tab ->
            WideNavigationRailItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(tab.label) },
                railExpanded = false,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun NavRailPreview() {
    PreviewSurface { NavRail(selected = Tab.Colors, onSelect = {}) }
}
