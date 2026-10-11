package dev.fathony.swatchbook

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

// The scrims enableEdgeToEdge uses by default behind a three-button navigation bar.
private val LightNavigationScrim = Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val DarkNavigationScrim = Color.argb(0x80, 0x1b, 0x1b, 0x1b)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            App(
                formFactor = deviceFormFactor(),
                wallpaperColorScheme = WallpaperColorScheme,
                onDarkThemeChange = ::matchSystemBars,
            )
        }
    }

    /**
     * The app's theme can differ from the system's, which is what the system bar icons follow
     * by default.
     */
    private fun matchSystemBars(darkTheme: Boolean) {
        enableEdgeToEdge(
            statusBarStyle =
                SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { darkTheme },
            navigationBarStyle =
                SystemBarStyle.auto(LightNavigationScrim, DarkNavigationScrim) { darkTheme },
        )
    }
}
