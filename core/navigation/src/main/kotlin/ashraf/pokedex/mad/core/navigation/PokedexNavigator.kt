package ashraf.pokedex.mad.core.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * Navigation contract used by UI layers.
 *
 * Why interface:
 * - Screens depend on this abstraction, not directly on NavBackStack/NavController.
 * - Makes navigation easier to test (can provide a fake navigator in tests).
 */
interface PokedexNavigator {

    /**
     * Push a new screen onto the back stack.
     */
    fun navigate(screen: PokedexScreen)

    /**
     * Pop the top screen if possible.
     *
     * @return true if a pop happened, false when there is nothing meaningful to pop
     *         (e.g., only the root screen remains).
     */
    fun navigateUp(): Boolean
}

/**
 * Navigation3-backed implementation of [PokedexNavigator].
 *
 * It delegates to Navigation3's [NavBackStack].
 */
class PokedexNavigatorImpl(
    private val backStack: NavBackStack<NavKey>,
) : PokedexNavigator {

    override fun navigate(screen: PokedexScreen) {
        // Add destination to the end of the back stack.
        backStack.add(screen)
    }

    override fun navigateUp(): Boolean {
        // Keep at least one root destination in the stack.
        return if (backStack.size > 1) {
            backStack.removeLastOrNull() != null
        } else {
            false
        }
    }
}