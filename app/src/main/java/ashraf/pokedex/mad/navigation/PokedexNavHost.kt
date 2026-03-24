package ashraf.pokedex.mad.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import ashraf.pokedex.mad.core.navigation.LocalComposeNavigator
import ashraf.pokedex.mad.core.navigation.PokedexNavigatorImpl
import ashraf.pokedex.mad.core.navigation.PokedexScreen

/**
 * App-level navigation host (reference-style).
 *
 * Why this lives in app (not core:navigation):
 * - This file wires *feature screens* together (home/details/settings).
 * - core:navigation keeps only reusable contracts (PokedexScreen, Navigator, locals).
 *
 * Current status:
 * - Uses placeholders for each destination until feature modules are implemented.
 * - Keeps the same structure as reference so swapping placeholders with real screens is easy.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokedexNavHost() {
    /**
     * Navigation3 back stack.
     * Start destination is Home.
     */
    val backStack = rememberNavBackStack(PokedexScreen.Home)

    /**
     * Strategy to render some destinations as dialogs.
     * (Reference uses Settings as dialog scene.)
     */
    val dialogStrategy = remember { DialogSceneStrategy<NavKey>() }

    /**
     * Our abstraction over back stack operations.
     * Screens call navigator.navigate(...) / navigateUp() instead of directly touching backStack.
     */
    val navigator = remember(backStack) { PokedexNavigatorImpl(backStack) }

    /**
     * Provide navigator to all composables via CompositionLocal.
     * Any child composable can access currentComposeNavigator.
     */
    CompositionLocalProvider(
        LocalComposeNavigator provides navigator,
    ) {
        /**
         * SharedTransitionLayout is kept now to match reference architecture.
         * Even if we are not doing advanced transitions yet, keeping this wrapper now
         * avoids structure changes later.
         */
        SharedTransitionLayout {
            NavDisplay(
                /**
                 * Source of truth for destinations.
                 */
                backStack = backStack,

                /**
                 * System/back action behavior: pop top destination if possible.
                 */
                onBack = { backStack.removeLastOrNull() },

                /**
                 * Enables dialog scenes for destinations marked with dialog metadata.
                 */
                sceneStrategy = dialogStrategy,

                /**
                 * Preserves saved state for each nav entry.
                 * Useful when returning to previous screens.
                 */
                entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator()),

                /**
                 * Destination mapping:
                 * NavKey type -> Composable content.
                 */
                entryProvider = entryProvider<NavKey> {
                    entry<PokedexScreen.Home> {
                        // TODO(Phase 5.4): Replace with PokedexHome(...) from feature:home.
                        // LocalNavAnimatedContentScope is imported to match reference usage.
                        val _animatedScope = LocalNavAnimatedContentScope.current
                        Text(text = "Home (placeholder)")
                    }

                    entry<PokedexScreen.Details> { screen ->
                        // TODO(Phase 5.5): Replace with PokedexDetails(...) from feature:details.
                        val _animatedScope = LocalNavAnimatedContentScope.current
                        Text(text = "Details (placeholder): ${screen.pokemon.name}")
                    }

                    entry<PokedexScreen.Settings>(
                        // Render settings as a dialog scene (reference style).
                        metadata = DialogSceneStrategy.dialog(),
                    ) {
                        // TODO(Phase 5.7.2): Replace with PokedexSettings(...) screen.
                        Text(text = "Settings (placeholder dialog)")
                    }
                },
            )
        }
    }
}