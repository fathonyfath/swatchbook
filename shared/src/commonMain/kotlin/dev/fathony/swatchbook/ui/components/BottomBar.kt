package dev.fathony.swatchbook.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * The bottom bar. [onSelect] also fires for the tab that is already selected.
 */
@Composable
fun BottomBar(selected: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    ShortNavigationBar(modifier = modifier) {
        Tab.entries.forEach { tab ->
            ShortNavigationBarItem(
                selected = tab == selected,
                onClick = { onSelect(tab) },
                icon = { Icon(tab.icon, contentDescription = null) },
                label = { Text(tab.label) },
            )
        }
    }
}

@ThemePreviews
@Composable
private fun BottomBarPreview() {
    PreviewSurface { BottomBar(selected = Tab.Colors, onSelect = {}) }
}
