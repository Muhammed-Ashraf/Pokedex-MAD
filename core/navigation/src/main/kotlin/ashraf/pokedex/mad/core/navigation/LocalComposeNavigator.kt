package ashraf.pokedex.mad.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

/**
 * CompositionLocal that provides the app navigator to the composable tree.
 *
 * Why this exists:
 * - Avoids passing navigator through every composable parameter.
 * - Any composable inside the provider can access navigation actions.
 */
val LocalComposeNavigator: ProvidableCompositionLocal<PokedexNavigator> =
    compositionLocalOf {
        error(
            "No PokedexNavigator provided! " +
                    "Make sure to provide a navigator at the app/root navigation host level.",
        )
    }

/**
 * Convenience accessor for the current navigator from CompositionLocal.
 *
 * Usage from any composable:
 *   val navigator = currentComposeNavigator
 *   navigator.navigate(PokedexScreen.Settings)
 */
val currentComposeNavigator: PokedexNavigator
    @Composable
    @ReadOnlyComposable
    get() = LocalComposeNavigator.current