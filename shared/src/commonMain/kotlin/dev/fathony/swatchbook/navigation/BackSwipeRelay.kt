package dev.fathony.swatchbook.navigation

import androidx.navigationevent.DirectNavigationEventInput
import androidx.navigationevent.NavigationEvent
import androidx.navigationevent.NavigationEventDispatcher
import androidx.navigationevent.NavigationEventDispatcherOwner
import androidx.navigationevent.NavigationEventHandler
import androidx.navigationevent.NavigationEventInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Sits between the system's back events and one NavDisplay.
 *
 * NavDisplay drives its own transition from the gesture and, on release, gives the outgoing
 * screen only the unplayed remainder of that transition. The card needs different timing: a
 * fixed fade on release and a fixed ease-back on cancel. So NavDisplay listens to a private
 * dispatcher instead of the real one, and this relay feeds it: the start of a swipe straight
 * away, which makes it compose the screen beneath, but the end only once the card's own
 * animation in [swipe] has finished.
 */
class BackSwipeRelay(
    private val swipe: BackSwipeState,
    private val scope: CoroutineScope,
    private val topContentKey: () -> Any,
) : NavigationEventHandler<NavigationEventInfo>(NavigationEventInfo.None, isBackEnabled = false),
    NavigationEventDispatcherOwner {

    /**
     * The dispatcher NavDisplay registers with; only this relay sends it events.
     */
    override val navigationEventDispatcher = NavigationEventDispatcher()

    private val input =
        DirectNavigationEventInput().also { navigationEventDispatcher.addInput(it) }

    /**
     * False while a swipe has no top screen to turn into a card, as when two panes are
     * showing; back then only acts on release.
     */
    var showsCard = true

    private var settling: Job? = null
    private var afterSettling: (() -> Unit)? = null

    override fun onBackStarted(event: NavigationEvent) {
        // A new swipe can begin while the last one is still settling.
        finishSettling()
        if (!showsCard) return
        swipe.start(topContentKey(), event)
        input.backStarted(event)
    }

    override fun onBackProgressed(event: NavigationEvent) {
        if (swipe.swipedKey != null) swipe.update(event)
    }

    override fun onBackCancelled() {
        if (swipe.swipedKey == null) return
        settle(animation = { swipe.restore() }) {
            input.backCancelled()
            swipe.reset()
        }
    }

    override fun onBackCompleted() {
        if (swipe.swipedKey == null) {
            // No card was shown: a back button, Android before 14, or two panes. NavDisplay
            // plays its ordinary transition.
            input.backCompleted()
        } else {
            // The swipe state is left as it is, card invisible, until the popped screen has
            // left the composition; SwipeNavDisplay resets it then.
            settle(animation = { swipe.fadeOut() }) { input.backCompleted() }
        }
    }

    private fun settle(animation: suspend () -> Unit, then: () -> Unit) {
        afterSettling = then
        settling = scope.launch {
            animation()
            settling = null
            finishSettling()
        }
    }

    private fun finishSettling() {
        settling?.cancel()
        settling = null
        afterSettling?.also { afterSettling = null }?.invoke()
    }

    fun dispose() {
        finishSettling()
        remove()
        navigationEventDispatcher.dispose()
    }
}
