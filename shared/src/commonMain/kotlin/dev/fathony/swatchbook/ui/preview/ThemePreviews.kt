package dev.fathony.swatchbook.ui.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.fathony.swatchbook.ui.theme.SwatchbookTheme

// Android's Configuration.UI_MODE_NIGHT_YES, spelled out to keep shared code free of
// android.* imports.
private const val UiModeNightYes = 0x20

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UiModeNightYes)
annotation class ThemePreviews

// A tablet in landscape. Setting the device, not just a size, also makes the window size class
// resolve as it would there.
private const val WideDevice = "spec:width=1280dp,height=800dp,dpi=240"

/**
 * Like [ThemePreviews], on a wide landscape screen.
 */
@Preview(name = "Light, wide", device = WideDevice)
@Preview(name = "Dark, wide", device = WideDevice, uiMode = UiModeNightYes)
annotation class WideThemePreviews

@Composable
fun PreviewSurface(content: @Composable () -> Unit) {
    SwatchbookTheme {
        Surface(color = MaterialTheme.colorScheme.background, content = content)
    }
}
