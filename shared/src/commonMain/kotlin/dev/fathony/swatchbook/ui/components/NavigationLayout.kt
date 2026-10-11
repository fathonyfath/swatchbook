package dev.fathony.swatchbook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews
import dev.fathony.swatchbook.ui.preview.WideThemePreviews

/**
 * Places [content] next to the tab navigation: above a bottom bar, or beside a rail when
 * [useRail] is set. [onSelect] also fires for the tab that is already selected.
 *
 * It takes the choice as a parameter instead of reading the window size, so each arrangement
 * can be previewed. [content] keeps one place in the composition in both, and with it its
 * saved state when the window crosses the breakpoint.
 */
@Composable
fun NavigationLayout(
    useRail: Boolean,
    selected: Tab,
    onSelect: (Tab) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(modifier.background(MaterialTheme.colorScheme.background)) {
        if (useRail) NavRail(selected, onSelect)
        Column(Modifier.weight(1f)) {
            // Whichever is showing already covers its side's system bar inset.
            val covered = if (useRail) {
                WindowInsets.systemBars.only(WindowInsetsSides.Start)
            } else {
                WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
            }
            Box(Modifier.weight(1f).consumeWindowInsets(covered)) { content() }
            if (!useRail) BottomBar(selected, onSelect)
        }
    }
}

@ThemePreviews
@Composable
private fun NavigationLayoutBarPreview() {
    PreviewSurface {
        NavigationLayout(
            useRail = false,
            selected = Tab.Colors,
            onSelect = {},
        ) {
            Text("Content")
        }
    }
}

@WideThemePreviews
@Composable
private fun NavigationLayoutRailPreview() {
    PreviewSurface {
        NavigationLayout(
            useRail = true,
            selected = Tab.Colors,
            onSelect = {},
        ) {
            Text("Content")
        }
    }
}
