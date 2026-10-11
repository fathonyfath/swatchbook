package dev.fathony.swatchbook.navigation

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList

/**
 * Screens shown inside a tab. Home is the tab's own list; the others open from it.
 */
sealed interface TabRoute {
    data object Home : TabRoute
    data class Detail(val hue: Int) : TabRoute
    data class Settings(val page: SettingsPage) : TabRoute
}

// The stacks are saved by hand rather than with rememberNavBackStack, which would bring in
// kotlinx.serialization for a handful of trivially encodable routes.

private const val HomeCode = -1

// Settings pages count down from here, below Home and every hue.
private const val FirstSettingsCode = -2

/**
 * Each route as an Int: a Detail is its hue, which is never negative.
 */
val TabStackSaver: Saver<SnapshotStateList<TabRoute>, Any> = listSaver(
    save = { stack ->
        stack.map { route ->
            when (route) {
                TabRoute.Home -> HomeCode
                is TabRoute.Detail -> route.hue
                is TabRoute.Settings -> FirstSettingsCode - route.page.ordinal
            }
        }
    },
    restore = { codes ->
        codes.map { code ->
            when {
                code >= 0 -> TabRoute.Detail(code)
                code == HomeCode -> TabRoute.Home
                else -> TabRoute.Settings(SettingsPage.entries[FirstSettingsCode - code])
            }
        }.toMutableStateList()
    },
)

val IntListSaver: Saver<SnapshotStateList<Int>, Any> = listSaver(
    save = { it.toList() },
    restore = { it.toMutableStateList() },
)
