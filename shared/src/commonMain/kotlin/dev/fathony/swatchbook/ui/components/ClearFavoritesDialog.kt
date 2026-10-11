package dev.fathony.swatchbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.fathony.swatchbook.ui.preview.PreviewSurface
import dev.fathony.swatchbook.ui.preview.ThemePreviews

/**
 * A dialog window shows nothing in a preview, so the card is a separate composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearFavoritesDialog(count: Int, onDismiss: () -> Unit, onClear: () -> Unit) {
    BasicAlertDialog(onDismissRequest = onDismiss) {
        ClearFavoritesCard(count = count, onCancel = onDismiss, onClear = onClear)
    }
}

@Composable
private fun ClearFavoritesCard(count: Int, onCancel: () -> Unit, onClear: () -> Unit) {
    Surface(
        shape = AlertDialogDefaults.shape,
        color = AlertDialogDefaults.containerColor,
        tonalElevation = AlertDialogDefaults.TonalElevation,
    ) {
        Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "Clear favorites?",
                color = AlertDialogDefaults.titleContentColor,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = if (count == 1) {
                    "This removes your saved color."
                } else {
                    "This removes all $count of your saved colors."
                },
                color = AlertDialogDefaults.textContentColor,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(onClick = onCancel) { Text("Cancel") }
                TextButton(onClick = onClear) { Text("Clear") }
            }
        }
    }
}

@ThemePreviews
@Composable
private fun ClearFavoritesCardPreview() {
    PreviewSurface { ClearFavoritesCard(count = 3, onCancel = {}, onClear = {}) }
}
