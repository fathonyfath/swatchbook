package dev.fathony.swatchbook.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner
import dev.fathony.swatchbook.navigation.transitions.HeldTransition
import dev.fathony.swatchbook.navigation.transitions.PopTransition
import dev.fathony.swatchbook.navigation.transitions.PushTransition

// Dark needs the stronger scrim: at 20% a dark card over a dimmed dark screen all but vanishes.
private const val LightScrimAlpha = 0.2f
private const val DarkScrimAlpha = 0.6f

/**
 * A NavDisplay with the app's motion: screens fade in place when pushed or popped, and a back
 * swipe turns the top screen into a card (see [backSwipe]).
 *
 * @param backEnabled false while something else owns system back, so that this display's
 *   handler cannot win over it.
 * @param showsSwipeCard false when the top screen is not shown alone, see [BackSwipeRelay].
 */
@Composable
fun <T : Any> SwipeNavDisplay(
    backStack: SnapshotStateList<T>,
    modifier: Modifier = Modifier,
    backEnabled: Boolean = true,
    showsSwipeCard: Boolean = true,
    sceneStrategies: List<SceneStrategy<T>> = emptyList(),
    entryProvider: (T) -> NavEntry<T>,
) {
    val systemDispatcherOwner = checkNotNull(LocalNavigationEventDispatcherOwner.current)
    val scope = rememberCoroutineScope()
    val swipe = remember { BackSwipeState() }
    val currentEntryProvider = rememberUpdatedState(entryProvider)
    val relay = remember(systemDispatcherOwner) {
        BackSwipeRelay(swipe, scope) { currentEntryProvider.value(backStack.last()).contentKey }
    }
    DisposableEffect(relay) {
        systemDispatcherOwner.navigationEventDispatcher.addHandler(relay)
        onDispose { relay.dispose() }
    }
    val canGoBack = backEnabled && backStack.size > 1
    SideEffect {
        relay.isBackEnabled = canGoBack
        relay.showsCard = showsSwipeCard
    }

    val scrimAlpha =
        if (MaterialTheme.colorScheme.background.luminance() < 0.5f) DarkScrimAlpha
        else LightScrimAlpha
    val swipeDecorator = remember(swipe, systemDispatcherOwner, scrimAlpha) {
        NavEntryDecorator<T> { entry ->
            val contentKey = entry.contentKey
            DisposableEffect(contentKey) {
                // A completed swipe leaves the card invisible until its screen is gone.
                onDispose { if (swipe.swipedKey == contentKey) swipe.reset() }
            }
            Box(Modifier.backSwipe(swipe, contentKey, scrimAlpha)) {
                // Screens get the real dispatcher back, so their own back handlers hear the system
                // rather than this display's relay.
                CompositionLocalProvider(
                    LocalNavigationEventDispatcherOwner provides systemDispatcherOwner,
                ) {
                    entry.Content()
                }
            }
        }
    }

    CompositionLocalProvider(LocalNavigationEventDispatcherOwner provides relay) {
        NavDisplay(
            backStack = backStack,
            modifier = modifier,
            onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
            entryDecorators =
                listOf(rememberSaveableStateHolderNavEntryDecorator(), swipeDecorator),
            sceneStrategies = sceneStrategies,
            transitionSpec = { PushTransition },
            popTransitionSpec = { PopTransition },
            predictivePopTransitionSpec = { HeldTransition },
            entryProvider = entryProvider,
        )
    }
}
