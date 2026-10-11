package dev.fathony.swatchbook.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fathony.swatchbook.navigation.SettingsPage
import dev.fathony.swatchbook.navigation.Tab
import dev.fathony.swatchbook.ui.components.AppScaffold
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * The Settings tab's list of categories.
 *
 * @param summary the line under each category's title, describing its current state.
 * @param selected the category open beside the list, when there is one to highlight.
 */
@Composable
fun SettingsScreen(
    summary: (SettingsPage) -> String,
    onOpen: (SettingsPage) -> Unit,
    selected: SettingsPage? = null,
) {
    AppScaffold(title = Tab.Settings.label) { padding ->
        Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
            SettingsPage.entries.forEach { page ->
                ListItem(
                    selected = page == selected,
                    onClick = { onOpen(page) },
                    supportingContent = { Text(summary(page)) },
                ) {
                    Text(page.title)
                }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun SettingsScreenPreview() {
    PreviewSurface {
        SettingsScreen(summary = { "Summary" }, onOpen = {}, selected = SettingsPage.Appearance)
    }
}
