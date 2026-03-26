package ashraf.pokedex.mad.core.preview

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import ashraf.pokedex.mad.core.designsystem.theme.PokedexTheme
import ashraf.pokedex.mad.core.navigation.LocalComposeNavigator
import ashraf.pokedex.mad.core.navigation.PokedexNavigator
import ashraf.pokedex.mad.core.navigation.PokedexScreen

/**
 * No-op navigator for previews.
 *
 * Why: real screens call [currentComposeNavigator] / [LocalComposeNavigator]. Outside the app
 * NavHost there is no [NavBackStack]. Providing this avoids crashes and keeps previews runnable.
 */
private object PreviewNavigator : PokedexNavigator {
    override fun navigate(screen: PokedexScreen) = Unit
    override fun navigateUp(): Boolean = false
}

/**
 * Wraps preview content with the same “tree” real UI expects:
 * - [LocalComposeNavigator] so navigation calls don’t crash.
 * - [PokedexTheme] so colors/background match the app.
 * - [SharedTransitionLayout] + [AnimatedContent] so composables that take `SharedTransitionScope` /
 *   `AnimatedContentScope` (e.g. home/detail shared element APIs) compile and preview like in [PokedexNavHost].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun PokedexPreviewTheme(
    content: @Composable androidx.compose.animation.SharedTransitionScope.(
        androidx.compose.animation.AnimatedContentScope,
    ) -> Unit,
) {
    CompositionLocalProvider(
        LocalComposeNavigator provides PreviewNavigator,
    ) {
        PokedexTheme {
            SharedTransitionLayout {
                AnimatedContent(targetState = Unit, label = "") {
                    content(this@SharedTransitionLayout, this)
                }
            }
        }
    }
}