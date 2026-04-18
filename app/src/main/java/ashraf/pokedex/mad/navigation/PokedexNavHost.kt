package ashraf.pokedex.mad.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
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
import ashraf.pokedex.mad.feature.details.PokedexDetails
import ashraf.pokedex.mad.feature.home.PokedexHome
import ashraf.pokedex.mad.feature.settings.PokedexSettings
import com.skydoves.compose.stability.runtime.TraceRecomposition

/**
 * App-level navigation host (reference-style).
 *
 * Why this lives in app (not core:navigation):
 * - This file wires *feature screens* together (home/details/settings).
 * - core:navigation keeps only reusable contracts (PokedexScreen, Navigator, locals).
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
@TraceRecomposition
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
         * SharedTransitionLayout is kept now.
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
                        PokedexHome(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = LocalNavAnimatedContentScope.current,
                        )
                    }


                    entry<PokedexScreen.Details> { screen ->
                        PokedexDetails(
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedContentScope = LocalNavAnimatedContentScope.current,
                            pokemon = screen.pokemon
                        )
                    }
                    entry<PokedexScreen.Settings>(
                        metadata = DialogSceneStrategy.dialog(),
                    ) {
                        PokedexSettings()
                    }
                },
            )
        }
    }
}